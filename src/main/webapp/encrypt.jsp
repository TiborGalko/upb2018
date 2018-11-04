<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>        
        <h1>UPB 2018 zadanie 3</h1>
        <a href="decrypt.jsp">Decrypt</a>
        <div>
            <h3>Choose file to encrypt</h3>
            <form action="upload" method="post" enctype="multipart/form-data">
                <input type="file" name="enc-file"> <br>                
                RSA public key: <input type="text" id="enc-rsa-pk" name="enc-rsa-pk"/><br>
                <input type="submit" value="Encrypt">                
            </form>                
        </div>
    </body>
</html>