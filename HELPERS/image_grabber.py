from bs4 import BeautifulSoup
import requests
import re
import urllib
import pandas as pd
from datetime import datetime,timedelta
import json
import sqlite3 as sql
import sys
import os.path
import time 

for page in range(11):
    print('\nLoading page {}...'.format(page))
    url = "http://www.imdb.com/list/ls058011111/?page={}".format(page)
    content = urllib.urlopen(url).read()
    soup = BeautifulSoup(content,'html.parser')
    print('Loaded the page')
    names = []
    imdbinx = []
    imgs = []
    for content in soup.findAll('a', href=True):
        if content['href'].find("/name/nm") != -1:
            names.append(str(content.get_text().encode('utf-8').strip()))
            imdbinx.append(str(content['href'].split('?ref')[0].split('/name/')[1]))
            if len(soup.findAll('img',alt = names[-1])) > 0:
                imgs.append(str(soup.findAll('img',alt = names[-1])[0]['src']))
            else:
                names.pop()
                imdbinx.pop()
    
    # prepare a DB
    print('Loading info to the db')
    conn = sql.connect('actors.sqlite')
    conn.text_factory = str
    cur = conn.cursor()
    cur.execute('''CREATE TABLE IF NOT EXISTS actors (id TEXT PRIMARY KEY, name TEXT)''')
    for i in range(len(names)):
        query = "INSERT OR IGNORE INTO actors VALUES(?,?)"
        cur.execute(query,(imdbinx[i],names[i],))
    conn.commit()
    cur.close()
    
    print('Downloading the images...\n')
    repeat = True
    while repeat:
        repeat = False
        for i in range(len(imgs)):
            if not os.path.exists("faces/{}.jpg".format(imdbinx[i].replace('/',''))):
                try:
                    urllib.urlretrieve(imgs[i], "faces/{}.jpg".format(imdbinx[i].replace('/','')))
                except Exception as e:
                    print(e)
                    repeat = True
                    break
                sys.stdout.write("\rLoaded {} images...".format(i + 1))


        time.sleep(5)
    
    