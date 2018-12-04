<%-- 
    Document   : page_1
    Created on : 29.11.2018, 15:36:18
    Author     : Kika
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
         <script
            src="https://code.jquery.com/jquery-3.3.1.min.js"
            integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
            crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
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
            .modalDialog > div {
                width: 400px;
                position: relative;
                margin: 10% auto;
                padding: 5px 20px 13px 20px;
                border-radius: 10px;
                background: #fff;
                background: -moz-linear-gradient(#fff, #999);
                background: -webkit-linear-gradient(#fff, #999);
                background: -o-linear-gradient(#fff, #999);
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
            #td_tr{
                font-size: 17px;
            }
            #submit_place{
                margin: 5% 18%;
                background-color: lightgray;
                height: 300px;
                width: 60%;
                border-radius: 25px 25px;
                font-size: 17px;
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
        <%
            final String message = (String) request.getAttribute ("message");                
            if (message != null) { %> 
                <script> alert("<%= message %>"); </script> 
        <% } %> 
        <header>
            <div class="table_">
            <table>
                <tr>
                <nav>
                    <ul>
                        <li><a href="encrypt">Encrypt</a></li>
                        <li><a href="decrypt">Decrypt</a></li>
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
                <form action="submitfile" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <input type="file" name="file-name">
                </div>
                <div class="form-group">
                    <label for="sel1">Share with: </label>
                    <input type="text" name="share-with">
                </div>
                <input type="submit" value="Submit" class="btn btn-primary btn-lg">                
            </form>    
            </div>
        </div>
        
        <script>
            console.log("script");
            $( document ).ready(function() {
                $.ajax({url: "filetable", success: function(result){
                    var array = JSON.parse(result);
                    var option = '';
                    option += '<tr>' + '<th>' + '<a href="#openModal">' + 'test' + '</a>' + '</td>' + '<td>Download</td> <td>Delete</td></tr>' ;
                    for(var i = 0; i < array.length; i++){
                        option += '<tr>' + '<th>' + '<a href="#openModal">' + array[i] + '</a>' + '</td>' + '<td>Download</td> <td>Delete</td></tr>' ;
                    }
                    
                    $('#table1').append(option);
                }, error: function(error){
                    console.log(error);
                }});
            });
        </script>
        
        <div id="td_tr">
            <div class="container col-6">
                <table class="table">
                    <thead id="table1" name="table1">
                        <tr>
                            <th>Name of document</th>
                            <th></th>
                            <th></th>
                        </tr>
                    </thead>                
                </table>       
            </div>
            <div id="openModal" class="modalDialog">
            <div>
                <a href="#close" title="Close" class="close">X</a>
                <h2>Detials:</h2>
                <p>Name of document: </p>
                <p>Put: </p>
                <p>Date: </p>
                <div>
                    <form>
                        <input type="text" name="comment" placeholder="Write a comment">
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
