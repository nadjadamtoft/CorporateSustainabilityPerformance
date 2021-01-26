# -*- coding: utf-8 -*-
from __future__ import unicode_literals
import spacy
import os
from collections import defaultdict, Counter
import csv
import re
import pandas as pd
import time
from spacy.matcher import Matcher


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
    2) Removes any characters except letters,—,-,',’,, and whitespaces
    3) Replaces multiple whitespaces in just one

    Parameters: 
    text - String

    Output: 
    Cleaned String
    '''
    # text to lowercase
    text = text.lower()
    # keep only letters, -, ' and space
    text = re.sub(r"[^A-Za-z—\-\'\’\, ]", ' ', text)
    # replace multiple whitespace with just one
    return re.sub(r"\s+", ' ', text)


if __name__ == "__main__":
    mainStartTime = time.time()
    # python -m spacy download en_core_web_sm
    nlp = spacy.load("en_core_web_md", max_length=1529140)
    pattern = [{'POS': 'AUX', 'OP': '*'},
               {'POS': 'VERB', 'OP': '+'}]
    matcher = Matcher(nlp.vocab)
    matcher.add("Verb phrase", None, pattern)
    count = list()
    # https://spacy.io/usage/linguistic-features#noun-chunks
    for doc in nlp.pipe(findTxts('TextFiles'), batch_size=25, n_process=2):
        spans = [doc[start:end] for _, start, end in matcher(doc)]
        count.extend(spans)
    df = pd.DataFrame({'VP': count})
    df['count'] = 1
    df.to_csv(
        'Counts/TestSpacyVP.csv', index=False)
    df = pd.read_csv('Counts/TestSpacyVP.csv', header=0)
    dfAgg = df.groupby(['VP']).sum()
    dfAgg.reset_index().sort_values(by='count', ascending=False).to_csv(
        'Counts/CSRVPWordFreqDict.csv', index=False)
    print("--- %s seconds --- for all" %
          (time.time() - mainStartTime))
