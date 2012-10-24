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
        'names': ('part', 'acc'),
        'formats': ('i', 'd')}, delimiter=',', skiprows=0)

plt.plot(data['part'], data['acc'],'-o',label="PME performance")
plt.plot(data['part'], data['part'] * (data['part'])*1.25e-4,'--',label="1.25e-4 * n^2")
ax = plt.axes()
ax.set_xlabel("Particles")
#ax.set_yscale('log')
ax.set_ylabel("Running time (ms)")
ax.set_title(title)
legend()
plt.show()
