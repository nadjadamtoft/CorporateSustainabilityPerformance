# -*- coding: utf-8 -*-
from __future__ import unicode_literals
import os
from collections import defaultdict, Counter
import csv
import re
import time
import pandas as pd


def reg_tokenize(regFilter, text):
    text = text.lower()
    text = re.sub(r"[^A-Za-z—\-\'\’ ]", ' ', text)
    text = re.sub(r"\s+", ' ', text)
    words = re.findall(regFilter, text)
    return words


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
                result.append(text)
    return result


def chooseType(choice):
    if choice == 1:
        return 'Economic'
    elif choice == 2:
        return 'Environmental'
    elif choice == 3:
        return 'Social'
    else:
        return 'Sustainability'


def createPattern(type):
    if type == 'Sustainability':
        dfEco = pd.read_excel(
            "SustainabilityDictionaries\\Economicdictionary.xlsx", sheet_name='Ark1', names=['words'])
        dfEnv = pd.read_excel(
            "SustainabilityDictionaries\\Environmentaldictionary.xlsx", sheet_name='Ark1', names=['words'])
        dfSoc = pd.read_excel(
            "SustainabilityDictionaries\\Socialdictionary.xlsx", sheet_name='Ark1', names=['words'])
        df = pd.concat([dfEco, dfEnv, dfSoc], ignore_index=True)
        wordList = df['words'].to_list()
        return "|".join((wordList))
    df = pd.read_excel(
        "SustainabilityDictionaries\\" + type + "dictionary.xlsx", sheet_name='Ark1', names=['words'])
    wordList = df['words'].to_list()
    return "|".join((wordList))


if __name__ == "__main__":
    mainStartTime = time.time()
    for value in range(1, 5):
        WORD = re.compile(createPattern(chooseType(value)))
        token_list = list()
        for txtFile in findTxts('TextFiles'):
            tokens = reg_tokenize(WORD, txtFile)
            token_list.extend(tokens)
        count = Counter(token_list)
        with open("Counts/" + chooseType(value) + "WordFreqDict.csv", "w+", newline="", encoding='utf-8') as csv_file:
            csv_file.write("%s,%s\n" % ('word', 'count'))
            for key, value in count.most_common():
                csv_file.write("%s, %s\n" % (key, value))
    print("--- %s seconds --- for all" %
          (time.time() - mainStartTime))
