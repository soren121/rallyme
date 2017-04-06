<!DOCTYPE html>
<html>
<head>
    <title>Organizer Registration &mdash; RallyMe</title>
    <meta charset="utf-8" />
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
                <h2>Organizer Registration</h2>
            </section>

            <form class="pure-form pure-form-stacked" action="Register" method="post">
                <fieldset>
                    <input class="pure-input-1" id="username" type="text" name="username" placeholder="Username" maxlength="50" required /> 
                    <input class="pure-input-1" id="password" type="password" name="password" placeholder="Password" required /> 
                    <input class="pure-input-1" id="email" type="email" name="email" placeholder="Email Address" maxlength="120" required /> 
                    <input class="pure-input-1" id="fname" type="text" name="firstname" placeholder="First Name" maxlength="50" required /> 
                    <input class="pure-input-1" id="lname" type="text" name="lastname" placeholder="Last Name" maxlength="50" required /> 

                    <input class="pure-input-1 pure-button pure-button-primary" type="submit" value="Sign up" />
                </fieldset>
            </form>
        </main>
    </div>

    <script type="text/javascript" async src="js/lib/svgxuse.min.js"></script>
</body>
</html>
