jax-rs-href
===========

A resource path resolver mechanism for JAX-RS.

A resource entity returned from a REST resource will often contain
links to other resources.  When a resource referenced in such a link
is part of the same REST service, it desirable to derive the path to 
the referenced resource using the same annotations that are used to 
direct HTTP requests to that resource.

This module provides a simple annotation-based mechanism that allows
a model builder for a resource to resolve a path to a referenced 
resource.
