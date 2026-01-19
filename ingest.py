import os
from dotenv import load_dotenv
from langchain.text_splitter import CharacterTextSplitter
from langchain_community.vectorstores import FAISS
from langchain.embeddings.openai import OpenAIEmbeddings
from loaders import load_documents

load_dotenv()
DATA_DIR = "data/"
VECTOR_DIR = "vector_store/"

def ingest():
    documents = []
    for file in os.listdir(DATA_DIR):
        path = os.path.join(DATA_DIR, file)
        try:
            docs = load_documents(path)
            documents.extend(docs)
        except Exception as e:
            print(f"Error loading {file}: {e}")

    if not documents:
        print("No documents loaded. Exiting.")
        return

    splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=100)
    docs = splitter.split_documents(documents)
    print(f"Split into {len(docs)} chunks")

    embeddings = OpenAIEmbeddings(openai_api_key=os.getenv("OPENAI_API_KEY"))
    db = FAISS.from_documents(docs, embeddings)
    db.save_local(VECTOR_DIR)
    print(f"Vector store saved to {VECTOR_DIR}")

if __name__ == "__main__":
    ingest()
