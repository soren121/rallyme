<!DOCTYPE html>
<html>
<head>
    <title>Edit Profile Page</title>
    <meta charset="utf-8" />
 
    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
    <link rel="stylesheet" type="text/css" href="css/pure-grids-responsive-min.css" />
    <link rel="stylesheet" type="text/css" href="css/dashboard.css" />
    <link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css" />
    <link rel="stylesheet" type="text/css" href="css/jquery-ui.theme.min.css" />
    <link rel="stylesheet" type="text/css" href="css/jquery.timepicker.min.css" />
</head>

<body>
    <header>
        <a id="logo" href="."><img src="images/logo.svg" alt="RallyMe" /></a>

        <nav class="pure-menu pure-menu-horizontal">
            <ul class="pure-menu-list">
                <li class="pure-menu-item pure-menu-selected"><a href="Dashboard" class="pure-menu-link">View your rallies</a></li>
                <li class="pure-menu-item"><a href="AddRally" class="pure-menu-link">Add new rally</a></li>
            </ul>
        </nav>

        <div class="header-buttons">
            <span>Welcome ${user.getFirstName()}!</span>
            
            <a href="Profile" class="pure-button">
                <svg class="icon"><use xlink:href="images/symbol-defs.svg#icon-user"></use></svg>
                Your Profile
            </a>
            
            <a href="Login?logout=true" class="pure-button pure-button-primary">
                <svg class="icon"><use xlink:href="images/symbol-defs.svg#icon-exit"></use></svg>
                Logout
            </a>
        </div>
    </header>

    <main>
        <h2>Edit Profile</h2>
        <#if error??>
            <p class="error">
                ${error}
            </p>
        </#if>

        <form class="pure-form pure-form-stacked pure-g" action="Profile" method="post">
            <fieldset class="pure-u-1 pure-u-md-1-2">
                <div class="w-box-left">
                    <label for="username">Username: </label> 
                    <input class="pure-input-1" id="username" type="text" name="Uname" value="${user.getUserName()}"
                        required /> 
                    
                    <label for="Firstname">First name: </label> 
                    <input class="pure-input-1" id="Firstname" name="Fname" value="${user.getFirstName()}"
                    required /></textarea> 
                    
                    <label for="Lastname">Last name: </label> 
                    <input class="pure-input-1" id="Lastname" type="text" name="Lname" value="${user.getLastName()}"
                    required /> 
                    
                    <label for="EmailAddress">Email Address: </label> 
                    <input class="pure-input-1" id="EmailAddress" type="email" name="Ename" value="${user.getEmail()}"
                    required /> 
                    
                    <label for="NewPassword">New Password: </label> 
                    <input class="pure-input-1" id="NewPassword" type="password" name="NewP"> 
                    
                    <label for="ConfirmNewPassword">Confirm New Password: </label> 
                    <input class="pure-input-1" id="CNewPassword" type="password" name="CNewP"> 
                    
                    <label for="CurrentPassword">Current Password: </label> 
                    <input class="pure-input-1" id="CurrentPassword" type="password" name="CurP"
                    required /> 
                       
                    <br />
                    <input class="pure-button pure-button-primary" type="submit" value="Submit" />
                </div>
            </fieldset>

        </form>
    </main>

    <script type="text/javascript" async src="js/lib/svgxuse.min.js"></script>
</body>
</html>
