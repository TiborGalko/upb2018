<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>        
        <h1>UPB 2018 zadanie 3</h1>
        <a href="encrypt.jsp">Encrypt</a>
        <div>
            <h3>Choose file to decrypt</h3>
            <form action="upload" method="post" enctype="multipart/form-data">
                <input type="file" name="dec-file"> <br>                
                RSA private key: <input type="text" id="dec-rsa-pk" name="dec-rsa-pk"/><br>
                <input type="submit" value="Decrypt">                
            </form>                
        </div>
    </body>
</html>