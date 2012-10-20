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
        'names': ('N', 'total time','total stddev','init time', 'init stddev'),
        'formats': ('i', 'd', 'd', 'd', 'd')}, delimiter=',', skiprows=1)

plt.plot(data['N'], data['init time'], label='Initialisation time')
plt.errorbar(data['N'], data['total time'],label='Total time', yerr=data['total stddev'])

ax = plt.axes()
ax.set_xlabel("System size (particles)")
ax.set_ylabel("Time (ms)")
ax.set_title(title)
pylab.legend()
pylab.ylim(1400,2000)
plt.show()

