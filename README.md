# ExtendedTilePuzzle

This project attempt to solve the Extended NxM tile puzzle (2 or 1-hole NxM tile puzzle) using various known graph search algorithms.
Each algorithm return a path from the initial state to the goal state with optimal cost or return that there is no path if the board have no solution. 


The problem consists of a NxM sized matrix which contains numbers and two or one empty places as follows:


| 1 | 2 | 3 | 4 |
| ------------- | ------------- | ------------- | ------------- |   
| 5  | 6 | 11 | 7 |
| 9  | 10 | 8 |  |

The final answer would be the moves up/down/left/right until we reach a sorted matrix in the following way:

| 1 | 2 | 3 | 4 |
| ------------- | ------------- | ------------- | ------------- |
| 5  | 6 | 7 | 8 |
| 9  | 10 |   |  |

The algorithms I used to solve the problem:
1. BFS 
2. DFID
3. A-star
4. IDA-star
5. DFBnB

* The huristic function used for A-star, IDA-star and DFBnB is Manahatten distance.
