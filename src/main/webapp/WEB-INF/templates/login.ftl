<!DOCTYPE html>
<html>
<head>
    <title>Organizer Login &mdash; RallyMe</title>
    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
    <link rel="stylesheet" type="text/css" href="css/login.css" />
</head>
<body>
	<div id="flex">
        <main>
            <section>
                <img id="logo" src="images/logo.svg" alt="RallyMe" />
                <h2>Organizer Login</h2>
            </section>

            <form class="pure-form pure-form-stacked" action="Login" method="post">
                <fieldset>
                    <input class="pure-input-1" id="username" type="text" name="username" placeholder="Username" required /> 
                    <input class="pure-input-1" id="password" type="password" name="password" placeholder="Password" required /> 

                    <input class="pure-input-1 pure-button pure-button-primary" type="submit" value="Submit" />
                </fieldset>
            </form>
        </main>
    </div>
</body>
</html>
