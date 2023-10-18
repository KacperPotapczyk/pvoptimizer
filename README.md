# Photovoltaic and energy storage optimizer

## Optimization problem
Optimizer solves following task. For given:
* photovoltaic cells power output,
* household power demand,
* contracts to purchase and sell electric energy from/to power grid,
* available storages and
* movable demands that have fixed profile but their start time can be adjusted.

Find:
* purchase and sell profiles from/to grid,
* charging and discharging profiles of storages,
* movable demands start time.

To minimize:
* operational costs. 

Subject to:
* power and energy constraints,
* storage charge/discharge power constraints,
* storage stored energy constraints.

## lp_solve
Optimization task is in form of mixed integer problem (MIP). It is solved using [lp_solve](https://lpsolve.sourceforge.net/5.5/) library.
To use this lp_solve in Java application, "Java wrapper" library is needed.
Instructions on building such library is available [here](https://lpsolve.sourceforge.net/5.5/Java/README.html).
In order to build this application it is expected that `lpsolve55j.jar` is installed in local maven repository.

## Usage
Communication with application is available through Kafka, one topic for incoming tasks to solve another for optimization results.
Messages have to be in avro format according to schemas in `resources/avro`.
Application can be deployed as container. Dockerfile requires to copy lp_solve wrapper libraries 
`liblpsolve55.so` and `liblpsolve55j.so` to container at build time.

After building image with application, entire environment with Zookeeper, Kafka and Schema Registry
can be created using `docker-compose -p pvoptimizer up -d`