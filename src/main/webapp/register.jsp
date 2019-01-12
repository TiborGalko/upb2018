<%-- 
    Document   : register
    Created on : 15.11.2018, 13:13:32
    Author     : h
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registration UPB z5</title>
        <script src="https://code.jquery.com/jquery-3.3.1.min.js" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    </head>
    <body>$
        <script>
            $(document).ready(function(){
                $("#generateKeysButton").click(function(){
                    $.ajax({
                        url: "generatekeys",
                        type: "GET",
                        dataType: "json",
                        contentType: "application/json",
                        mimeType: "application/json",
                        
                        success: function(result){
                            $("#reg-pub-key").val(result.PublicKey);
                            $("#reg-priv-key").val(result.PrivateKey);
                        }
                    });
                });
            });
        </script>
        <div class="container">
            <div class="jumbotron">
                <h1 class="display-4">Register UPB2018</h1>
                <hr class="my-4">                  
                <%
                final String message = (String) request.getAttribute ("message");                
                if (message != null) { %> 
                <script> alert("<%= message %>"); </script>     
                <% } %>
            </div>                
        <div>
            <form action="register" method="post">
                <div class="form-group">
                    <label for="login">Meno:</label>
                    <input type="text" class="form-control" id="login" name="login" required>
                </div>
                <div class="form-group">
                    <label for="password">Heslo:</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <div class="form-group">
                    <label for="reg-pub-key">Verejný RSA kľúč</label>
                    <input type="text" class="form-control" id="reg-pub-key" name="reg-pub-key"required>
                </div>
                <div class="form-group">
                    <label for="reg-priv-key">Privátny RSA kľúč</label>
                    <input type="text" class="form-control" id="reg-priv-key" name="reg-priv-key"required>
                </div>
                <input type="submit" value="Registrovať sa" class="btn btn-primary">  
                <button type="button" class="btn btn-success" id="generateKeysButton">Generate keys</button>
            </form>            
        </div>
            <a href="login.jsp">Späť na prihlásenie</a>
        </div>
    </body>
</html>
