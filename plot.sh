#!/bin/bash

if [ "$1" = "-l" ]; then
    gnuplot <<EOF
set terminal png
set output 'validCodeGrowth.png'
set logscale y 2
plot 'data.dat' using 1:2 with lines title 'number of valid codes'
EOF

else
    gnuplot <<EOF
set terminal png
set output 'validCodeGrowth.png'
plot 'data.dat' using 1:2 with lines title 'number of valid codes'
EOF

fi
