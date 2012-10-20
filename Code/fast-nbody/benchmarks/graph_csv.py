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
        'names': ('N', 'total time','init time'),
        'formats': ('i', 'i', 'i')}, delimiter=',', skiprows=1)

plt.plot(data['N'], data['init time'], label='Initialisation time')
plt.plot(data['N'], data['total time'],label='Total time')
ax = plt.axes()
ax.set_xlabel("System size (particles)")
ax.set_ylabel("Time (ms)")
ax.set_title(title)
legend()
plt.show()
