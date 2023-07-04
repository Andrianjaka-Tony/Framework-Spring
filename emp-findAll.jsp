<%@page import="model.Emp, java.util.Set, java.util.HashMap"%>
<%
  HashMap<String, Object> data = (HashMap<String, Object>) request.getAttribute("data");
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
</head>
<body>

  <h1>Hello</h1>
  
  <%
    Set<String> keys = data.keySet();
    for (String key : keys) {
      Emp e = (Emp) data.get(key);
      out.println("<p>" + e.getName() + " " + e.getAge() + "</p>");
    }
  %>

</body>
</html>