<%-- 
    Document   : submit
    Created on : 29.11.2018, 15:42:28
    Author     : Kika
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
        <title>Submit document</title>
        <style>
            #submit_place{
                margin: 5% 18%;
                background-color: lightgray;
                height: 400px;
                width: 60%;
                border-radius: 25px 25px;
            }
            #logout{
                margin-left: 91%
            }
            #table__{
                margin: 2% 18%;
            }
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
            #txt{
                margin-left: 5%;
                padding: 3%;
            }
        </style>
    </head>
    <body>
        <header>
            <div class="table_">
            <table>
                <tr>
                    <nav>
                        <ul>
                            <li><a href="encrypt">Encrypt</a></li>
                            <li><a href="decrypt">Decrypt</a></li>
                            <li><a href="submit">Submit document</a></li>
                            <li><a href="share">Share document</a></li>
                            <div id="logout">
                                <li><a href="logout">Log out</a></li>
                            </div>
                        </ul>
                    </nav>
                </tr>
            </table>
            </div>
        </header>
        
        <div id="submit_place">
            <div style="font-size: 17px; margin-left: 13%; padding: 5%;">
                <b>Here you can upload and share your document with another registration user</b>
            </div>
            <div id="txt">
                <form action="upload" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <input type="file" name="dec-file">
                </div>
                <div class="form-group">
                    <label for="sel1">Share with: </label>
                    <select class="form-control" id="sel1">
                        <option>Kika</option>
                        <option>Tibor</option>
                        <option>Juraj</option>
                        <option>Karol</option>
                    </select>
                </div>
                <input type="submit" value="Submit" class="btn btn-primary">                
            </form>    
            </div>
        </div>
    </body>
</html>
