set style data lines 
set xrange [0:120]
set yrange [0:500]
set label 'Size of Army: 500' at 10, 230
set label 'dStart: 10000(millis)' at 10, 250
set label 'dInfectious: 20000(millis)' at 10, 270
set label 'dRecovered: 10000(millis)' at 10, 290
set xlabel "time in seconds"
set ylabel "number"
plot 'result.txt' using ($1/1000):2 with lines lc 2 lw 2 title "Susceptible" , 'result.txt' using ($1/1000):3 with lines lc rgb 'red' lw 2 title "Infected" , 'result.txt' using ($1/1000):4 with lines lc rgb '#AAAAFF' lw 2 title "Recovered" 
 pause -1 "Hit return to close"

