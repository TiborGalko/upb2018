<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Decryption tool</title>
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    </head>
    <body>        
        <div class="container"> 
            <div class="jumbotron">
                <h1 class="display-4">Encryption / Decryption tool</h1>
                <hr class="my-4">
                <a href="encrypt"><button class="btn btn-info"><i class="material-icons">lock</i><span>Encrypt</span></button></a>
                <a href="logout"><button class="btn btn-danger float-right"><i class="material-icons">exit_to_app</i><span>Log out</span></button></a>
            </div>                
        <div>
            <h3>Choose file to decrypt</h3>
            <form action="upload" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <input type="file" name="dec-file">
                </div>
                <div class="form-group">
                    <label for="dec-rsa-pk">RSA private key:</label>
                    <input type="text" class="form-control" id="dec-rsa-pk" name="dec-rsa-pk" required>
                </div>
                <input type="submit" value="Decrypt" class="btn btn-primary">                
            </form>            
        </div>
        </div>
    </body>
</html>