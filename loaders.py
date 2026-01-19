from langchain_community.document_loaders import PyPDFLoader, CSVLoader, TextLoader

def load_documents(file_path):
    if file_path.endswith(".pdf"):
        loader = PyPDFLoader(file_path)
    elif file_path.endswith(".csv"):
        loader = CSVLoader(file_path)
    elif file_path.endswith(".txt"):
        loader = TextLoader(file_path)
    else:
        raise ValueError("Unsupported file type")
    return loader.load()
