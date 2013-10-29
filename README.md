### SMServlet - Self Mapping Java Servlet


By extending SMservlet (standard servlet v. 2.5) it is easy to create a lightweight REST-like mechanism by mapping servlet's methods by using of annotations similar to @RequestMapping from Spring MVC.

Additionally, it contains a simple serialization/deserialization and result handling mechanism which can be easily extended by providing onwn types and by implementing of the provided interfaces.

For example, when the client sends the POST request to the server using http://server/data/addContact/2 as url 
while having contact data in JSON format in parameter called contact_data, 
the servlet method should be annotated the following way in order to be called.

```java
public class ExampleServlet extends SelfMappedServlet {
  @RequestMapping("data/addContact/{id}")
  public Object addContact(@PathVariable("id") String id, @Attribute(type=Format.JSON, value="contact") Contact c) {
    // do something with Contact object and personId 
  }
}
```
For more examples please have a look in the wiki pages linked below.

#### Quick start

> ##### Installation

#### Documentation

> #####[Request Mapping](https://github.com/mara-mfa/smservlet/wiki/Request-Mapping)

> ##### Attribute deserialization

> ##### Response serialization


#### Extending

> ##### Custom serialization / deserialization

> ##### Custom response handling
