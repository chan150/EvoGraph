set terminal postscript eps enhanced  size 3in,1.5in
set output 'output.eps'
set nokey
set format xy "10^%L"
set logscale xy
FILES = system("ls -1 temp/part-*")
plot for [data in FILES] data using 1:2 with points pointtype 5 pointsize 0.2 lt rgb "red"
