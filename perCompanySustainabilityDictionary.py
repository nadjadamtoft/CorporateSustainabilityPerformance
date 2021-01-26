# -*- coding: utf-8 -*-
from __future__ import unicode_literals
import os
import csv
import re
import pandas as pd
import time


def cleanText(text):
    '''
    Function that receives a string. The transformation it applies on the string are the following:
    1) Sets it to lowercase
    2) Removes any characters except letters, —, -, ', ’, , and whitespaces
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
    dfEco = pd.read_excel(
        "SustainabilityDictionaries\\Economicdictionary.xlsx", sheet_name='Ark1', names=['words'])
    dfEnv = pd.read_excel(
        "SustainabilityDictionaries\\Environmentaldictionary.xlsx", sheet_name='Ark1', names=['words'])
    dfSoc = pd.read_excel(
        "SustainabilityDictionaries\\Socialdictionary.xlsx", sheet_name='Ark1', names=['words'])
    ecoPattern = "|".join(dfEco['words'].to_list())
    envPattern = "|".join(dfEnv['words'].to_list())
    socPattern = "|".join(dfSoc['words'].to_list())
    ecoWORD = re.compile(ecoPattern)
    envWORD = re.compile(envPattern)
    socWORD = re.compile(socPattern)
    WORD = re.compile(r'[A-Za-z—\-\'\’]*')
    with open("Counts/CorporateSustainability.csv", "w+", newline="", encoding='utf-8') as csv_file:
        csv_file.write("%s,%s,%s,%s\n" % ('file', 'Economic sustainability',
                                          'Environmental sustainability', 'Social sustainability'))
        for root, dirs, files in os.walk("TextFiles"):
            for file in files:
                if file.endswith('.txt'):
                    filePath = open('TextFiles\\'+file,
                                    'r', encoding='utf-8')
                    text = filePath.read()
                    cleanedText = cleanText(text)
                    ecoTokens = len(re.findall(ecoWORD, text))
                    envTokens = len(re.findall(envWORD, text))
                    socTokens = len(re.findall(socWORD, text))
                    totalTokens = len(re.findall(WORD, text))/500
                    csv_file.write("%s, %.4f, %.4f, %.4f\n" % (
                        file, round(ecoTokens/totalTokens, 4), round(envTokens/totalTokens, 4), round(socTokens/totalTokens, 4)))

    print("--- %s seconds --- for all" % (time.time() - mainStartTime))
