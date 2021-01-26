import fitz  # this is pymupdf
import os


def findPdfs(path):
    result = list()
    for root, dirs, files in os.walk(path):
        for file in files:
            if file.endswith('.pdf'):
                result.append(file)
    return result


def writeToText(path, pdfName):
    with fitz.open(path+pdfName) as doc:
        text = ""
        for page in doc:
            text += page.getText()

    text_file = open(
        "TextFiles/" + pdfName[:-4]+".txt", "w+", encoding="utf-8")
    text_file.write(text)
    text_file.close()


if __name__ == "__main__":
    for pdfs in findPdfs("PdfFiles/"):
        writeToText("PdfFiles/", pdfs)
