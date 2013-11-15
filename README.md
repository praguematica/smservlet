SMServlet allows user to easily map incoming http requests to servlet's methods by using annotations similar to this:
```java
@RequestMapping(value="data/getUser/{id}", responseFormat=Format.JSON)
public Object getUser(@PathVariable("id") String id) {
   User user = getUserByIdFromSomewhere(id);
   return user;
}
```

***

The mapping itself is inspired by Spring MVC @RequestMapping. The motivation is to have such mechanism without having to use whole spring mvc as well as when being limited by using older servlet specification 2.5. 
Additionally to the request mapping, smServlet does the following:
* deserialization of attributes from JSON (using gson)
* serialization of response to JSON (using gson)
* contains mechanism to add **custom serialization and deserialization formats**
* allows to implement **custom logic** of handling of a response
* support for downloading respose as a file (attachment)
* see [documentation, guide and examples](../../wiki)
* see [installation and quick start guide](https://github.com/mara-mfa/smservlet/wiki/Installation) for getting started


