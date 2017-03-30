<!DOCTYPE html>
<html>
<head>
    <title>Organizer Login &mdash; RallyMe</title>
    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
    <link rel="stylesheet" type="text/css" href="css/login.css" />
</head>
<body>
	<div id="flex">
        <#if error??>
            <p class="error">
                ${error}
            </p>
        </#if>

        <main>
            <section>
                <a href="."><img id="logo" src="images/logo.svg" alt="RallyMe" /></a>
                <h2>Organizer Login</h2>
            </section>

            <form class="pure-form pure-form-stacked" action="Login" method="post">
                <fieldset>
                    <input class="pure-input-1" id="username" type="text" name="username" placeholder="Username" required /> 
                    <input class="pure-input-1" id="password" type="password" name="password" placeholder="Password" required /> 

                    <input class="pure-input-1 pure-button pure-button-primary" type="submit" value="Login" />

                    <p><em>Don't have an account yet?</em></p>
                    <a class="pure-input-1 pure-button" href="Register">Sign up</a>
                </fieldset>
            </form>
        </main>
    </div>
</body>
</html>
