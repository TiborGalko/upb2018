<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Encryption tool</title>
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <style>
            .table_{
                margin: 0.5% 1%;
                background-color: lightgray;
                height: 80px;
                width: 98%;
                border-radius: 50px 25px
            }
            nav ul{
                margin: 0 1%;
                list-style-type: none;
            }
            nav ul li{
                float: left;
                padding: 0 1%;
                margin: 0 5px;
                font-size: 25px;
                height: 73px;
                line-height: 3.2em;
            }
            a{
                color: black;
            }
            #logout{
                margin-left: 91%
            }
        </style>
    </head>
    <body>
        <%
            final String message = (String) request.getAttribute("message");
            if (message != null) {%> 
        <script> alert("<%= message%>");</script> 
        <% }%>
        <nav class="table_">
            <ul>
                <li><a href="encrypt">Encrypt</a></li>
                <li><a href="decrypt">Decrypt</a></li>        
                <div id="logout">
                    <li><a href="logout">Log out</a></li>
                </div>
            </ul>
        </nav> 
        <div class="container">          
            <div>
                <h3>Choose file to encrypt</h3>
                <form action="upload" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <input type="file" name="enc-file" required>
                    </div>
                    <div class="form-group">
                        <label for="enc-rsa-pk">RSA public key:</label>
                        <input type="text" class="form-control" id="enc-rsa-pk" name="enc-rsa-pk" required>
                    </div>
                    <input type="submit" value="Encrypt" class="btn btn-primary">                
                </form>            
            </div>
        </div>
    </body>
</html>