# 14 Proxy and Adapter Patterns 

Software modules sometimes need to interact with other services that may need some "middle management" for various reasons. For example

* Your client software needs to interface with a database (our frequently bogeyman example in this course). The database can be massive, and initialising the requisite libraries in order to query a database can be an expensive operation (e.g., since it has to establish connections to the DB possibly over the network). We probably don't want each client interaction to have to initialise the DB drivers/libraries each time the DB is being accessed. We can use some middle management layer to help with this.
* You need to perform some logging upon every action involving the service. Adding logging code to every location in the client code that interfaces with the service can tedious and error-prone. A logging layer between your client and service code can handle all of the logging requests. Each request to the service gets logged before being passed to the service.
* Finally, much more relevant to your upcoming project, you may want to implement _caching_ for recurring requests to the external service. In this case, the "external service" I'm referring to would be a file containing data that is too large to fit in memory.

## The Proxy design pattern

The proxy design pattern is simple in principle.

You have some client code that interfaces with some external service, and for whatever reason (see examples above), you need to mediate or control access to that service.
You create a _Proxy_ class that "pretends" to be the external service, and your client only interfaces with that Proxy, though they are not aware that they're not getting "the real thing". 

So in the DB example above, you would add a Proxy class that:

* initialises the DB libraries if they haven't already been initialised (i.e., "lazy loads" the DB drivers)
* provides access to the DB through those libraries

This is as opposed to the proxy-less solution with each client having to keep track of their own copies of the DB libraries, or the clients having to re-initialise the DB drivers each time.

So the Proxy design pattern involves the following pieces:

* **Client**: The client is the code that needs to interact with the service (e.g., writing to or querying a database, making HTTP requests)
* **Service**: The service is some class that provides useful functionality, but using it may require some additional processing (e.g., lazy loading, logging, caching, access controls).

You can get a lot done with just the two pieces above. The Proxy pattern becomes useful when you need the extra processing steps before and/or after using the Service.
To do that, you add the following:

* **ServiceInterface**: Declare the interface of the service (i.e., the "public surface" where other modules are expected to interact with the service). Make your `Service` implement this interface.
* **Proxy**: The Proxy also implements the `ServiceInterface`. This allows it to disguise itself as the service. 
  - The `Proxy` holds a reference to the real `Service`. Since it adheres to the same interface as the `Service`, it has the same method signatures. Those methods can now (1) perform preprocessing, (2) transfer the request to the real `Service`, (3) perform post-processing, and (4) send a response back to the `Client`
  - The `Proxy`'s reference to the service could possibly also be declared as a `Proxy`. This allows _further_ proxy processing to be added if needed (where the `Proxy` is now a "client" and _its_ reference is the "service".
* **The `Client` holds a reference to the `Proxy`, and not the `Service`.**  The `Client` has no idea that there is a Proxy sitting between it and the real `Service`.

### Pros and cons

The Proxy pattern is helpful in the following ways

* The proxy can control service objects without clients knowing about it. If additional processing needs to be added when a service is used, it can be added without modifying either the client _or_ the service. **_Open/closed principle_**, anyone?
* You can manage the lifecycle of the proxy when clients don't care about it. This may result in more efficient setup and teardown (memory management) of heavyweight services.
* This final one is more of a benefit in the _software design process_. If you create a Proxy that sits between your client and a heavyweight service (or a complicated service you haven't implemented yet), you can implement a simpler "fake" version of the service that you client can use for the time being or for testing. Using a Proxy in conjunction with dependency injection can be a useful strategy in these situations.

Two drawbacks are:

* Like many of the design patterns we've talked about this quarter, the code can become more complicated, since it involves the addition of new classes. As always, it's up to you to decide if some design choice is "overkill", or if it will be a useful investment (in terms of time and simplicity) to avoid technical debt further down the line.
* If you have heavy-enough proxies sitting between your client and the service, responses from the service might get delayed. This kind of defeats the purpose of the pattern; your Proxy is now kind of a heavyweight service itself.

## The Adapter design pattern

The Adapter pattern is similar in intent to the Proxy pattern. You have two modules that need to interact with each other, but need some "middle management" in order to do this.

In the Proxy pattern, our possible reasons were because the second module in this "handshake" is a heavyweight Service, or a service that needs some additional pre- and post-processing while it's being used.  

However there is another reason for requiring "middle management" between client and service: they are not interoperable.

As a real-world example, consider the different power plugs used in different countries. Each time I visit home in India, I need adapters in order to plug in my laptop charger (American plugs) into the sockets at home (Indian sockets). Similarly, we've used used dongles to connect projects or external monitors to our laptops (variously, with HDMI, USB-C, Thunderbolt, or, god forbid, VGA sockets).
This "handshake" between two systems that are not interoperable is enabled by the use of an _Adapter_ that "translates" between the two systems.

So the pattern looks very familiar to the Proxy pattern:

You have

* A **Client**, again, that needs to interact with...
* A **Service**, which is NOT interoperable with the service, for some reason (competing standards, properietary software, old and new software, etc.) So to enable the interaction between these systems, you create...
* An **Adapter** that _adapts_ the service so that it is usable by the client. The Adapter exposes a public interface that the Client interacts with. Unlike the Proxy pattern, that public interface is _not_ the same as the Service's interface.

One software example is if you're trying to merge, compare, or analyse data coming from two sources, but one source emits data in JSON format, and another emits data in XML format. If your client only works in JSON format, you can create an Adapter that converts XML data to JSON data, and your Client uses that Adapter.

This promotes adherence to the **_Single Responsibility Principle_**, because your Client is not being bloated with data conversion code.

For example, consider the `[InputStreamReader](https://download.java.net/java/early_access/jdk20/docs/api/java.base/java/io/InputStreamReader.html)` class. It is a self-described "bridge from byte streams to character streams: It reads bytes and decodes them into characters using a specified `charset`". 

