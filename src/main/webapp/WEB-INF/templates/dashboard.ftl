<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
	<link rel="stylesheet" type="text/css" href="css/dashboard.css" />
    <title>Organization Rally Page</title>
</head>

<body>
	<header>
        <a id="logo" href="."><img src="images/logo.svg" alt="RallyMe" /></a>
        <h1>Organizer Dashboard</h1>
	</header>

    <main>
       
        <table class="pure-table pure-table-striped">
            <thead>
                <tr>
                    <th>Rally name</th>
                    <th>Description</th>
                    <th>Twitter handle</th>
                    <th>Start time</th>
                    <th>Latitude</th>
                    <th>Longitude</th>
                    <th>&nbsp;</th>
                </tr>
            </thead>

            <tbody>
              <#list rallylist as rally>
                	<tr>
                    <td>${rally.getName()}</td>
                    <td>${rally.getDescription()}</td>
                    <td>${rally.getTwitterHandle()}</td>
                    <td>${rally.getstartTime()}</td>
                    <td>${rally.getLatitude()}</td>
                    <td>${rally.getLongitude()}</td>
                   
                    <td><a id="organizer-button" class="pure-button" href="AddRally">Edit Rally</a></td>
                </tr>
                
           	</#list>
             </tbody>
        </table>
        
        <p>
            <a id="organizer-button" class="pure-button pure-button-primary" href="AddRally">Add Rally</a>
        </p>
    </main>
</body>
</html>
