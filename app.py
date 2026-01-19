import os
import re
import json
import datetime
import dateparser
from dateparser.search import search_dates
import streamlit as st
from ingest import ingest
from qa_chain import load_qa_chain

st.set_page_config(page_title="Campus360 AI Assistant", page_icon="AI")
st.title("Campus360 AI Assistant")

# Build vector store on first run
if not os.path.isdir("vector_store") or not os.listdir("vector_store"):
    st.info("No index found. Ingesting documents...")
    ingest()
    st.success("Ingestion complete.")

st.header("Ask a question or request a booking")

def load_user_context():
    """Load user context from JSON file written by desktop app, fallback to env vars."""
    ctx_path = "user_context.json"
    if os.path.isfile(ctx_path):
        try:
            with open(ctx_path, "r", encoding="utf-8") as f:
                data = json.load(f)
            return (
                data.get("id", "AI-USER"),
                data.get("name", "AI Assistant"),
                data.get("email", ""),
                data.get("role", "Student"),
            )
        except Exception:
            pass
    return (
        os.getenv("AI_USER_ID", "AI-USER"),
        os.getenv("AI_USER_NAME", "AI Assistant"),
        os.getenv("AI_USER_EMAIL", ""),
        os.getenv("AI_USER_ROLE", "Student"),
    )

USER_ID, USER_NAME, USER_EMAIL, USER_ROLE = load_user_context()

qa = load_qa_chain()
query = st.text_input("Type your message below:")

# -------------------------------------
# Booking logic setup
# -------------------------------------
BOOKING_RULES = {
    "classroom": {"filename": "classrooms.txt", "resource": "Classroom", "status": "Confirmed"},
    "lab": {"filename": "labs.txt", "resource": "Lab", "status": "Pending Approval"},
    "sports": {"filename": "sports.txt", "resource": "Sports Facility", "status": "Pending Approval"},
    # Event scheduling is handled in the desktop UI, not by the AI assistant
}

def extract_datetime(text):
    """Extract a future date/time; prefer explicit time, default to tomorrow 10:00."""
    result = search_dates(
        text,
        settings={"PREFER_DATES_FROM": "future", "RETURN_AS_TIMEZONE_AWARE": False},
    )
    if result:
        dt = result[0][1]
    else:
        dt = datetime.datetime.now() + datetime.timedelta(days=1)
        dt = dt.replace(hour=10, minute=0, second=0, microsecond=0)

    # If no time was found (midnight), default to 10:00
    if dt.hour == 0 and dt.minute == 0:
        dt = dt.replace(hour=10, minute=0)
    return dt

def detect_resource_type(query_lower):
    if any(word in query_lower for word in [
        "sports facility", "sport", "sports", "field", "court", "pitch", "gym", "stadium", "arena", "pool", "swim", "swimming"
    ]):
        return "sports"
    if any(word in query_lower for word in ["lab", "laboratory", "lab room"]):
        return "lab"
    if "event" in query_lower:
        return "event"
    if any(word in query_lower for word in ["classroom", "class room", "room", "class"]):
        return "classroom"
    return None

def ai_handle_booking(query):
    """Handle booking requests and persist them to the same files as the desktop app."""
    query_lower = query.lower()
    if not any(word in query_lower for word in ["book", "reserve", "schedule", "request"]):
        return None

    resource_key = detect_resource_type(query_lower)
    if not resource_key:
        return None

    # Do not auto-schedule events via AI; let the dedicated UI handle event creation/details.
    if resource_key == "event":
        return None

    rule = BOOKING_RULES[resource_key]

    # Extract date and time
    dt = extract_datetime(query)
    date = dt.strftime("%Y-%m-%d")
    start_time = dt.strftime("%H:%M")
    end_time = (dt + datetime.timedelta(hours=1)).strftime("%H:%M")

    # Extract facility/room name
    # Capture facility keyword plus optional identifier (e.g., "court A"), but ignore date/time words
    match = re.search(r"(room|lab|field|court|classroom|pool|gym)(?:\s+([A-Za-z0-9-]+))?", query_lower)
    room_base = match.group(1).upper() if match else "TBD"
    suffix = match.group(2) if match and match.group(2) else ""
    if suffix in ["today", "tomorrow", "tonight", "morning", "evening", "afternoon"]:
        suffix = ""
    room = f"{room_base} {suffix}".strip()

    role = USER_ROLE.title()
    resource = rule["resource"]
    status = rule["status"]

    # Save in the same 9-field, comma-space format used by the desktop app files
    fields = [USER_ID, USER_NAME, role, resource, room, date, start_time, end_time, status]
    line = ", ".join(fields) + "\n"

    try:
        filename = rule["filename"]
        with open(filename, "a", encoding="utf-8") as f:
            f.write(line)
    except Exception as e:
        return f"Could not save booking: {e}"

    if status == "Pending Approval":
        return f"Your {resource.lower()} booking request for {room} on {date} at {start_time} has been sent for admin approval."
    else:
        return f"Your {resource.lower()} booking for {room} on {date} at {start_time} has been confirmed."

# -------------------------------------
# Handle user input
# -------------------------------------
if query:
    # Try booking first
    booking_response = ai_handle_booking(query)
    if booking_response:
        st.sidebar.success(f"Logged in as: {USER_NAME} ({USER_ROLE})")
        st.markdown("### Assistant Action")
        st.success(booking_response)
    else:
        # If not booking, do Q&A
        with st.spinner("Thinking..."):
            result = qa(query)
        st.markdown("### Answer")
        st.write(result["result"])
else:
    st.sidebar.info(f"Logged in as: {USER_NAME} ({USER_ROLE})")
