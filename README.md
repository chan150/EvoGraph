# EvoGraph [SIGKDD18 (to appear)]
EvoGraph: An Effective and Efficient Graph Upscaling Method for Preserving Graph Properties


## Usage (Simple example)
 - Download source codes by
``
git clone https://github.com/chan150/EvoGraph.git
`` 

 - Compile 
``
./compile.sh
``
(for distributed environment) or 
``
./compile-local.sh
``
(for a single machine) 

 - Execute EvoGraph in a single machine
``
./run-local.sh output -gs.input toy -gs.sf 2
``

 - Execute EvoGraph in distributed machines
``
./run.sh output -gs.input toy -gs.sf 2
``

## Usage (Detail example)
 - upscaling a toy graph with a scale factor 2 by using 120 machines/threads 
``
./run.sh output -gs.input toy -gs.sf 2 -m 120
``


## Parameters
 - **-gs.input** input path in master computer
 - **-gs.sf** scale factor
 - **-machine** or **-m** number of machines/threads (e.g. -m 120)
 
## Parameters (optional for a large input file)
 - **-gs.eid** number of edges in original graph (optional; e.g. -gs.eid 10427)
 - **-gs.vid** number of vertices in original graph (optional; e.g. -gs.vid 1024)

## License
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Citing the paper
For more details about EvoGraph, please refer to [our paper(to appear)](http://infolab.dgist.ac.kr/~mskim/papers/SIGKDD18.pdf).

If you use EvoGraph, please cite:
```

```
