# Envision

Envision is a small, easy to use Clojure library for data processing, cleanup
and visualisation. If you've heard about Incanter, you may see a couple of things
that we do in a similar way. 

You can check out a couple of rendered examples [here](http://coffeenco.de/articles/envision/templates/index_file.html).

## Project Maturity

Envision is a relatively young project. Since it's never meant to be used in hard-
production (e.g. it will never be something user-facing), and is intended to be 
used by people who'd like to yield some information from their data, it should 
be stable enough from the very early releases.

## Dependency Information (Artifacts)

Envision artifacts are [released to Clojars](https://clojars.org/clojurewerkz/envision). If you are using Maven, add the following repository
definition to your `pom.xml`:

```xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

### The Most Recent Version

With Leiningen:

``` clojure
[clojurewerkz/envision "0.1.0-SNAPSHOT"]
```

With Maven:

``` xml
<dependency>
  <groupId>clojurewerkz</groupId>
  <artifactId>envision</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## General Approach

Main idea of this library is to make exploratory analysis more interactive and visual,
although in programmer's way. Envision creates a "throwaway environment" every time
you, for example, make a line chart. You can modify chart the way you want, change
all the possible configuration parameters, filter data, add exponents the ways we 
wouldn't be able to program for you.

We concluded that visual environments are often constraining, and creating an API
for every since feature would make it amazingly big and bloated. So we do a bare 
minimum, which is already helpful by default through the API and let you configure
everything you could've possibly imagined yourself: adding interactivity, combining
charts, customizing layouts and so on.

## Usage

Main entrypoint is `clojurewerkz.envision.core/render`. It creates a temporary
directory with all the required dependencies and returns you a path to it. For example,
let's generate some data and render a line and area charts:

```clj
(ns my-ns
  (:require [clojurewerkz.envision.core         :as envision]
            [clojurewerkz.envision.chart-config :as cfg]

(envision/render
   [(envision/histogram 10 (take 100 (distribution/normal-distribution 5 10))
               {:tick-format "s"})

    (envision/linear-regression
     (flatten (for [i (range 0 20)]
                [{:year (+ 2000 i)
                  :income (+ 10 i (rand-int 10))
                  :series "series-1"}
                 {:year (+ 2000 i)
                  :income (+ 10 i (rand-int 20))
                  :series "series-2"}]
                ))
     :year
     :income
     [:year :income :series])
    (cfg/make-chart-config
     {:id            "line"
      :headline      "Line Chart"
      :x             "year"
      :y             "income"
      :x-config      {:order-rule "year"}
      :series-type   "line"
      :data          (flatten (for [i (range 0 20)]
                                [{:year (+ 2000 i)
                                  :income (+ 10 i (rand-int 10))
                                  :series "series-1"}
                                 {:year (+ 2000 i)
                                  :income (+ 10 i (rand-int 20))
                                  :series "series-2"}]
                                ))
      :series        "series"
      :interpolation :cardinal
      })
    (cfg/make-chart-config
     {:id            "area"
      :headline      "Area Chart"
      :x             "year"
      :y             "income"
      :x-config      {:order-rule "year"}
      :series-type   "area"
      :data          (into [] (for [i (range 0 20)] {:year (+ 2000 i) :income (+ 10 i (rand-int 10))}))
      :interpolation :cardinal
      })
    ])
```

Function will return a tmp folder path, like: 

```
/var/folders/1y/xr7zvp2j035bpq09whg7th5w0000gn/T/envision-1402385765815-3502705781
```

`cd` into this path and start an HTTP Server on most systems you'd have Python 2.7 installed.

```
python -m SimpleHTTPServer
```

After that you can point your browser to 

```
http://localhost:4000/templates/index.html
```

If you don't want to start an HTTP server, or don't have Python installed, just open `templates/index_file.html` 
static file in your browser.

You can check out a couple of example graphs rendered as static files [here](http://coffeenco.de/articles/envision/templates/index_file.html).

We decided to use an simple HTTP server by default, since sometimes `d3` doesn't like `file://` protocol. However, 
you can always just open `templates/index_file.html` in your browser and get pretty much same result.

## Chart configuration

In order to configure chart, you have to specify:

  * `id`, a unique string literal identifying the chart
  * `data`, sequence of maps, where each map represents an entry to be displayed
  * `x`, key that should be taken as `x` value for each rendered point
  * `y`, key that should be taken as `y` value for each rendered point
  * `series-type`, one of `line`, `bubble`, `area` and `bar` for line charts, Scatterplots, 
     area charts and barcharts, correspondingly     

Optionally, you can specify: 

  * `series`, which will split your data, grouping or color-coding charts by given keys
    keys should be given either as a string or a vector or strings.
  * `interpolation`, interpolation type to be used in area or line chart, usually you want
    to use `linear`, `basis`, or `step-after`, but there're more options, which will be
    mentioned in a corresponding section.
  * `x-config` specifies a configuration for X axis
  
`x-config` options:
  * `order-rule` specifies a key to sort data points on `x` axis, if it's not `x` 
  * `override-min` overrides minimum for an axis
  
## Features:

 * Histograms 
 * Scatterplots
 * Boxplots
 * Barcharts
 * Regression lines
 * Cluster visualisation

## Supported Clojure Versions

Envision supports Clojure 1.4+.

## Community

To subscribe for announcements of releases, important changes and so on, please follow
[@ClojureWerkz](https://twitter.com/#!/clojurewerkz) on Twitter.


## Envision Is a ClojureWerkz Project

Envision is part of the [group of libraries known as ClojureWerkz](http://clojurewerkz.org), together with
[Monger](http://clojuremongodb.info), [Elastisch](http://clojureelasticsearch.info), [Langohr](http://clojurerabbitmq.info),
[Welle](http://clojureriak.info), [Titanium](http://titanium.clojurewerkz.org) and several others.

## Development

Envision uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make
sure you have it installed and then run tests against all supported Clojure versions using

```
lein2 all test
```

Then create a branch and make your changes on it. Once you are done with your changes and all
tests pass, submit a pull request on Github.

## License

Copyright © 2014 Alex Petrov, Michael S. Klishin 

Double licensed under the Eclipse Public License (the same as Clojure) or the Apache Public License 2.0.

## Credits

Development sponsored by [codecentric AG](http://codecentric.de)

![Development Sponsored](https://www.codecentric.de/wp-content/themes/ccHomepage/img/logo-codecentric.png)

[dimple](http://dimplejs.org/), [d3](http://d3js.org) and [Twitter Bootstrap](http://getbootstrap.com/) sources 
belong to their respective owners and are lincensed on different terms, not contradicting to library license.
