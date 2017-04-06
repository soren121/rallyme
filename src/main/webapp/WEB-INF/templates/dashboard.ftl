<!DOCTYPE html>
<html>
<head>
    <title>Organization Rally Page</title>
    <meta charset="utf-8" />

    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
    <link rel="stylesheet" type="text/css" href="css/pure-grids-responsive-min.css" />
	<link rel="stylesheet" type="text/css" href="css/dashboard.css" />
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
        <h2>Your rallies</h2>

        <table class="pure-table pure-table-striped">
            <thead>
                <tr>
                    <th>Rally name</th>
                    <th>Start time</th>
                    <th>Location</th>
                    <th>&nbsp;</th>
                </tr>
            </thead>

            <tbody>
              <#list rallylist as rally>
                	<tr>
                    <td>${rally.getName()}</td>
                    <td>${rally.getStartTime()}</td>
                    <td>${rally.getLocation()}</td>
                   
                    <td><a id="organizer-button" class="pure-button" href="AddRally">Edit Rally</a></td>
                </tr>
                
           	</#list>
             </tbody>
        </table>
        
        <p>
            <a id="organizer-button" class="pure-button pure-button-primary" href="AddRally">Add Rally</a>
        </p>
    </main>

    <script type="text/javascript" async src="js/lib/svgxuse.min.js"></script>
</body>
</html>
