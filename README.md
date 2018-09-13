# EvoGraph [SIGKDD18]
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
For more details about EvoGraph, please refer to [our paper](https://dl.acm.org/authorize?N667133).
You can download the paper authorized in [my github.io site](https://chan150.github.io/EvoGraph/).

If you use EvoGraph, please cite:
```
@inproceedings{Park:2018:EEE:3219819.3220123,
 author = {Park, Himchan and Kim, Min-Soo},
 title = {EvoGraph: An Effective and Efficient Graph Upscaling Method for Preserving Graph Properties},
 booktitle = {Proceedings of the 24th ACM SIGKDD International Conference on Knowledge Discovery \&\#38; Data Mining},
 series = {KDD '18},
 year = {2018},
 isbn = {978-1-4503-5552-0},
 location = {London, United Kingdom},
 pages = {2051--2059},
 numpages = {9},
 url = {http://doi.acm.org/10.1145/3219819.3220123},
 doi = {10.1145/3219819.3220123},
 acmid = {3220123},
 publisher = {ACM},
 address = {New York, NY, USA},
 keywords = {barabasi-albert, graph generation, graph upscaling, parallel computation, preferential attachment},
} 
```
