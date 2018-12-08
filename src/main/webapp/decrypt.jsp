<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Decryption tool</title>
        <script
            src="https://code.jquery.com/jquery-3.3.1.min.js"
            integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
        crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons"> 
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">                          
        <style>
            .modalDialog {
                position: fixed;
                font-family: Arial, Helvetica, sans-serif;
                top: 0;
                right: 0;
                bottom: 0;
                left: 0;
                background: rgba(0,0,0,0.8);
                z-index: 99999;
                opacity:0;
                -webkit-transition: opacity 400ms ease-in;
                -moz-transition: opacity 400ms ease-in;
                transition: opacity 400ms ease-in;
                pointer-events: none;           
            }
            .modalDialog:target {
                opacity:1;
                pointer-events: auto;
            }
            .modalDiv {
                width: 60%;
                position: relative;
                margin: 3% auto;
                padding: 30px 20px 80px 20px;
                border-radius: 10px;
                background: #d3d2d1;
                background: -moz-linear-gradient(#d3d2d1, #c6c6c4);
                background: -webkit-linear-gradient(#d3d2d1, #c6c6c4);
                background: -o-linear-gradient(#d3d2d1, #c6c6c4);                
            }
            .comment {                
                background: white;
                padding: 4px;
                border: 1px dashed black;
                margin-bottom: 10px;
            }
            .close {
                background: #606061;
                color: #FFFFFF;
                line-height: 25px;
                position: absolute;
                right: -12px;
                text-align: center;
                top: -10px;
                width: 24px;
                text-decoration: none;
                font-weight: bold;
                -webkit-border-radius: 12px;
                -moz-border-radius: 12px;
                border-radius: 12px;
                -moz-box-shadow: 1px 1px 3px #000;
                -webkit-box-shadow: 1px 1px 3px #000;
                box-shadow: 1px 1px 3px #000;
            }
            .close:hover{ 
                background: #00d9ff; 
            }
            #logout{
                margin-left: 91%
            }                        
            a{
                color: black;
            }                        
            #logout{
                margin-left: 91%
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
            #commentInput {
                width: 100%;
                margin-bottom: 10px;
            }
            td:hover {
                background-color: lightgray;
            } 
            form {
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body>
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
                <h3>Choose file to decrypt or pick from table</h3>
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
            <script>
                console.log("script");
                $(document).ready(function () {
                    $.ajax({url: "filetable", success: function (result) {
                            if (result) {
                                var array = JSON.parse(result);
                                var option = '';
                                for (var i = 0; i < array.length; i++) {
                                    option += '<tr>' +
                                            '<td onclick="openDetailsModal(\'' + array[i] + '\')"><a href="#openModal">' + array[i] + '</a></td>' +
                                            '<td onclick="openCommentsModal(\'' + array[i] + '\')"><a href="#openComment">Comment</a></td>' +
                                            '<td onclick="decryptModalFilename(\'' + array[i] + '\')"><a href="#decryptModal">Decrypt and download</a></td>' +
                                            '<td onclick="deleteFile(\'' + array[i] + '\')">Delete</td>' +
                                            '</tr>';
                                }

                                $('#table1').append(option);
                            }
                        }, error: function (error) {
                            console.log(error);
                        }});
                });
                function openDetailsModal(field) {
                    $("#fileName").val(field);
                }
                function openCommentsModal(field) {
                    $("#fileNameComment").val(field);
                    $.post("filetable", $.param({"fileName": field}), function (result) {
                        if (result) {
                            var array = JSON.parse(result);
                            var option = '';
                            for (var i = 0; i < array.length; i++) {
                                option += "<div class='comment'> " + array[i] + "</div>";
                            }
                            console.log(option);
                            $('#comments').html(option);
                        }
                    });
                }
                /*Vymaze subor a spravi update tabulky*/
                function deleteFile(filename) {                    
                    $.post("filetable", $.param({"deleteFile": filename}), function (result) {
                        $("#table1 tr").remove();
                        if (result) {                            
                            var array = JSON.parse(result);
                            var option = '';
                            for (var i = 0; i < array.length; i++) {
                                option += '<tr>' +
                                        '<td onclick="openDetailsModal(\'' + array[i] + '\')"><a href="#openModal">' + array[i] + '</a></td>' +
                                        '<td onclick="openCommentsModal(\'' + array[i] + '\')"><a href="#openComment">Comment</a></td>' +
                                        '<td>Decrypt and download</td>' +
                                        '<td onclick="deleteFile(\'' + array[i] + '\')">Delete</td>' +
                                        '</tr>';
                            }

                            $('#table1').append(option);
                            console.log(option);
                        }
                    });
                }
                function decryptModalFilename(filename) {
                    $("#dec-filename").val(filename);
                }
            </script>
            <table class="table">
                <thead>
                    <tr>
                        <th>Name of document</th>
                        <th></th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>   
                <tbody id="table1"></tbody>
            </table>
            <div id="openModal" class="modalDialog">
                <div class="modalDiv">
                    <a href="#close" title="Close" class="close">X</a>
                    <h2>Sharing:</h2>
                    <div>
                        <form action="submitfile" method="post">
                            <input type="hidden" id="fileName" name="fileName" value="#">
                            <div class="form-group">
                                <label for="sel1">Share with: </label>
                                <input type="text" name="share-with" required>
                            </div>
                            <input type="submit" value="Submit" class="btn btn-primary btn-lg">                
                        </form> 
                    </div>
                </div>
            </div>
            <div id="openComment" class="modalDialog">
                <div class="modalDiv">
                    <a href="#close" title="Close" class="close">X</a>
                    <h2>Add a comment: </h2>                
                    <div>
                        <form action="submitfile" method="post">
                            <input type="hidden" id="fileNameComment" name="fileNameComment" value="#">                        
                            <div>                               
                                <input type="text" id="commentInput" name="comment" placeholder="Write a comment"> 
                            </div>
                            <input type="submit" value="Submit" class="btn btn-primary btn-lg">                
                        </form> 
                    </div>
                    <div>
                        <h2>Comments:</h2>
                        <div id="comments"></div>
                    </div>
                </div>
            </div>
            <div id="decryptModal" class="modalDialog">
                <div class="modalDiv">
                    <a href="#close" title="Close" class="close">X</a>
                    <h2>Decrypt file:</h2>
                    <div>
                        <form action="upload" method="post">
                            <div class="form-group">
                                <label for="dec-filename">File: </label>
                                <input type="text" id="dec-filename" name="dec-filename" value="#" readonly>
                            </div>
                            <div class="form-group">
                                <label for="dec-rsa-pk-file">RSA private key:</label>
                                <input type="text" class="form-control" id="dec-rsa-pk-file" name="dec-rsa-pk-file" required>
                            </div>
                            <input type="submit" value="Decrypt" class="btn btn-primary btn-lg">                
                        </form>                        
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>