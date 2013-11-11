### Self Mapping Servlet - smservlet

Creating a new servlet by extending smservlet allows user to easily map incoming http requests to servlet's methods by using annotations. The mapping itself is inspired by Spring MVC @RequestMapping. The motivation is to have such mechanism without having to use whole spring mvc as well as when being limited by using older servlet specification 2.5. 
Additionally to the request mapping, smServlet does the following:
* deserialization of attributes from JSON (using gson)
* serialization of response to JSON (using gson)
* contains mechanism to add **custom serialization and deserialization formats**
* allows to implement **custom logic** of handling of a response
* support for downloading respose as a file (attachment)
* see [Documentation](https://github.com/mara-mfa/smservlet/wiki/_pages) for details
* see [Installation and quick start guide](https://github.com/mara-mfa/smservlet/wiki/0.-Installation) for getting started

***

To start with, see the following simple example:
```java
public class ExampleServlet extends SelfMappedServlet {
  @RequestMapping("data/getUser/{id}")
  public Object getUser(@PathVariable("id") String id) {
    // do something
  }
}
```

Smservlet contains a deserialization mechanism which automaticaly grabs the data from the request and deserialize them to the java object, for example, to map the request with url `data/addUser` which contains POST variable called `user` and this variable contains data in the JSON format, use the following:
```java
@RequestMapping("data/addUser")
public Object addUser(@Attribute(value="user", format=Format.JSON) User user) {
  // do something
}
```
Similarly, you can specify the output format of the result. The following example shows how to return results in JSON format.
```java
@RequestMapping(value="data/getUser/{id}", responseFormat=Format.JSON)
public Object getUser(@PathVariable("id") String id) {
   User user = getUserByIdFromSomewhere(id); // get user details from somewhere
   return user;
```
Content type can be specified as well:
```java
@RequestMapping(value="data/getUser/{id}", responseFormat=Format.JSON, contentType="application/json")
public Object getUser(@PathVariable("id") String id) {
   User user = getUserByIdFromSomewhere(id);
   return user;
}
```
There are 3 objects which, if declared in the function signature, are automatically set by smservlet.
* HttpSession
* HttpServletRequest
* HttpServletResponse

```java
@RequestMapping("data/doSomething")
public Object doSomething(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
   // do something
}
```

If you need to something extra with the results apart from printing them into the output stream, you can implement your own response handler by implementing ResponseHandler interface. For example
```java
public class CustomResponseHandler implements ResonseHandler {
   public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err) {
      // do something
   }
   public void result(HttpServletResponse response, Object result, RequestMapping rm) {
      // do something when exception was thrown
   }
}
```
This handler is then assigned to the method by the following way
```java
@RequestMapping(value="data/getUser/{id}", responseHandler=CustomResponseHandler.class)
public Object getUser(@PathVariable("id") String id) {
   User user = getUserByIdFromSomewhere(id);
   return user;
}
```
Both **responseHandler** and **responseFormat** can be used on the servlet level by using @SelfMapped annotation, so you don't have to specify it for each of the methods, as seen in the following example:
```java
@SelfMapped(responseFormat=Format.JSON, responseHandler=CustomResponseHandler.class)
public class ExampleServlet extends SelfMappedServlet {
  @RequestMapping("data/getUser/{id}")
  public Object getUser(@PathVariable("id") String id) {
    // do something
  }
  @RequestMapping("data/addUser")
  public Object addUser(@Attribute(value="user", format=Format.JSON) User user) {
    // do something
  }
}
```
### Downloading files
There are two different ways how to handle the attachments.
##### By using annotations
For downloading files, there is an appropriate ResponseHandler called **FileAttachmentHandler** part of the library. In addition to this handler, there is another parameter called **fileName**
Example:
```java
@RequestMapping("data/getUser/{id}", responseHandler=FileAttachmentHandler.class, fileName="user-details.txt", content-type="text/plain")
public Object getUser(@Attribute(value="id") String id) {
   User user = getUserByIdFromSomewhere(id);
   return user;
}
```
##### Programatically
If you need a bit of more control, for example, when you need to generate the filename based on the data, you can achieve this by returning **FileAttachment** object. See the following example:
```java
@RequestMapping("data/getUser/{id}", responseHandler=FileAttachmentHandler.class)
public Object getUser(@Attribute(value="id") String id) {
   User user = getUserByIdFromSomewhere(id);
   return new FileAttachment("text-plain", "user-details-" + id, user);
}
```



