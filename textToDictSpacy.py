# -*- coding: utf-8 -*-
from __future__ import unicode_literals
import spacy
import os
from collections import defaultdict, Counter
import csv
from spacy.tokenizer import Tokenizer
import re
import time


def findTxts(path):
    '''
    Function that receives a path for text files to read them and put them all in an array. 
    It does this by going to the path, checking each file there and taking those that end in .txt. 
    It then reads from the files and appends them to a list that is returned at the end of the execution.
    The files should be in the TextFiles/ path relative to where the script is placed.

    Parameters:
    path - String

    Output:
    A list containing the texts in TextFiles/ 
    '''
    result = list()
    for root, dirs, files in os.walk(path):
        for file in files:
            if file.endswith('.txt'):
                filePath = open('TextFiles/'+file,
                                'r', encoding='utf-8')
                text = filePath.read()
                result.append(cleanText(text))
    return result


def cleanText(text):
    '''
    Function that receives a string. The transformation it applies on the string are the following:
    1) Sets it to lowercase
    2) Removes any characters except letters,—,-,',’ and whitespaces
    3) Replaces multiple whitespaces in just one

    Parameters: 
    text - String

    Output: 
    Cleaned String
    '''
    # text to lowercase
    text = text.lower()
    # keep only letters, -, ' and space
    text = re.sub(r"[^A-Za-z—\-\'\’\ ]", ' ', text)
    # replace multiple whitespace with just one
    return re.sub(r"\s+", ' ', text)


if __name__ == "__main__":
    nlp = spacy.load("en_core_web_lg", max_length=1529140)
    # Instantiate the tokenizer
    tokenizer = Tokenizer(nlp.vocab)
    result = list()
    # Pipe will make it possible for the code to be multiprocessed so it scales
    for tokens in tokenizer.pipe(findTxts('TextFiles'), batch_size=50):
        # Save the array of word positions
        result.extend(tokens.to_array("ORTH"))
    pos_counts = Counter(result)
    # 'w' open for writing '+' open a disk file for updating (reading and writing)
    with open("Counts/CSRWordFreqDict.csv", mode="w+", newline="", encoding='utf-8') as csv_file:
        # headers of csv
        csv_file.write("%s,%s\n" % ('word', 'count'))
        # most_common() returns a list of ordered tuples with the key and count
        for orth_id, count in pos_counts.most_common():
            csv_file.write("%s, %d\n" %
                           (tokens.vocab.strings[orth_id], count))
