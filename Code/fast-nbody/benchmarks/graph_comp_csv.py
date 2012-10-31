#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import os
import sys
from pylab import *

fileName = sys.argv[1]
title = sys.argv[2]

with open(fileName) as f:
    data = np.loadtxt(f, dtype={
        'names': ('particles', 'spme_total', 'spme_init', 'fma_total', 'fma_init', 'bas_total', 'bas_init'),
        'formats': ('i', 'i', 'i', 'i', 'i', 'i', 'i')}, delimiter=',', skiprows=0)

plt.plot(data['particles'], data['spme_total'],'-o',label="PME")
plt.plot(data['particles'], data['fma_total'],'-v',label="FMA")
plt.plot(data['particles'], data['bas_total'],'-s',label="Basic algorithm")
ax = plt.axes()
ax.set_xlabel("Particles")
#ax.set_yscale('log')
ax.set_ylabel("Running time (ms)")
ax.set_title(title)
legend()
plt.show()
