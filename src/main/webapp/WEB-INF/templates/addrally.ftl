<!DOCTYPE html>
<html>
<head>
    <title>Add Rally Page</title>
    <meta charset="utf-8" />
    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
	<link rel="stylesheet" type="text/css" href="css/dashboard.css" />
</head>

<body>
	<header>
        <a id="logo" href="."><img src="images/logo.svg" alt="RallyMe" /></a>
        <h1>Add Rally</h1>
        <a href="Login?logout=true" class="pure-button pure-button-primary">Logout</a>
	</header>

    <main>
            <form class="pure-form pure-form-stacked" action="AddRally" method="post">
                <fieldset>
                	<legend>Rally Information:</legend>
                	
                	Event Name:<br>
                    <input class="pure-input-1" id="name" type="text" name="name" placeholder="name" required /> 
                    
                    Event Description:<br>
                    <input class="pure-input-1" id="description" type="text" name="description" placeholder="description" required /> 
                    
                    TwitterHandle:<br>
                    <input class="pure-input-1" id="twitterHandle" type="text" name="twitterHandle" placeholder="twitterHandle" required /> 
                    
                    Event Start Time:<br>
                    <input class="pure-input-1" id="startTime" type="text" name="startTime" placeholder="startTime" required /> 
                    
                    Event area:<br>
                    <input class="pure-input-1" id="latitude" type="text" name="latitude" placeholder="latitude" required /> 
                    <input class="pure-input-1" id="longitude" type="text" name="longitude" placeholder="longitude" required /> 

					<br>
                    <input class="pure-input-1 pure-button pure-button-primary" type="submit" value="Submit Event" />

                </fieldset>
            </form>                
        <p>
            <a id="organizer-button" class="pure-button pure-button-primary" href="submitRally">Submit</a>
        </p>
    </main>
</body>
</html>
