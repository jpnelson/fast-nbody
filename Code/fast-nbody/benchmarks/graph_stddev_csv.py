#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import os
import sys
import pylab

fileName = sys.argv[1]
title = sys.argv[2]

with open(fileName) as f:
    data = np.loadtxt(f, dtype={
        'names': ('N', 'acc','stddev'),
        'formats': ('i', 'd', 'd')}, delimiter=',', skiprows=1)

plt.errorbar(data['N'], data['acc'],fmt='--o',label='Accuracy', yerr=data['stddev'])

ax = plt.axes()
ax.set_xlabel("N: Mesh levels")
ax.set_ylabel("Error")
ax.set_title(title)
ax.set_xticklabels([0,4,5,6,7,8], rotation='horizontal')
pylab.legend()
#pylab.ylim(1400,2000)
pylab.xlim(3.5,8.5)
plt.show()

