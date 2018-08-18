"""
This code fetches all images of the actors and encodes them.

After encoding the images the encoded files are saved as numpy array and kept in encoder directory 


"""
from datetime import datetime,timedelta
import json
import sqlite3 as sql
import sys
import os.path
import os
import time
import face_recognition as fr
from PIL import Image
import numpy as np
import time

actors = os.listdir('new_face2')[1:]
actors.sort()

ids = []
for actor in actors:
    ids.append(int(actor.split('_')[0]))
    
ids_actors = zip(ids,actors)
keydict = dict(zip(ids,actors))
actors = list(keydict.values())

encodes = [[],[],[],[],[],[],[],[],[],[],[]]
main_counter = 0
ccc = 0

for actor_dir in actors:
    if ccc % 30 == 0 and ccc > 0:
        time.sleep(3)
    if ccc < main_counter:
        ccc += 1
        continue
    sys.stdout.write('\r{}'.format(actor_dir))
    dirr = 'new_face2/{}'.format(actor_dir)
    files = os.listdir(dirr)
    imgnames = [i for i in files if i.endswith('.jpg') or i.endswith('.jpeg') or i.endswith('.png')]
    cnt = 0
    temp_enc = []
    for imgname in imgnames:
        path = dirr + '/' + imgname 
        img = fr.load_image_file(path)
        enc = fr.face_encodings(img)
        if len(enc) > 0 and cnt < len(encodes):
            temp_enc.append(enc[0])
            cnt += 1
        if cnt >= len(encodes):
            break
    if cnt < len(encodes):
        print('\nOnly Found {} Encodes Check!!!!'.format(cnt))
        break
    else:
        m = 0
        for te in temp_enc:
            encodes[m].append(te)
            m += 1
    ccc += 1
    main_counter += 1  

    
cc = 2
for encode in encodes:
    np.save('encodes/1_{}_1000encdngs'.format(cc),encode)
    cc += 1