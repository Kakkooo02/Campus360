import os
from dotenv import load_dotenv
from langchain.chains import RetrievalQA
from langchain.llms.openai import OpenAI
from langchain.vectorstores import FAISS
from langchain.embeddings.openai import OpenAIEmbeddings

load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

def load_qa_chain():
    """Loads the LangChain Retrieval-based QA model."""
    embeddings = OpenAIEmbeddings(openai_api_key=OPENAI_API_KEY)
    db = FAISS.load_local("vector_store", embeddings, allow_dangerous_deserialization=True)
    retriever = db.as_retriever(search_kwargs={"k": 5})

    llm = OpenAI(temperature=0.0, openai_api_key=OPENAI_API_KEY)
    qa = RetrievalQA.from_chain_type(
        llm=llm,
        retriever=retriever,
        return_source_documents=True
    )
    return qa
