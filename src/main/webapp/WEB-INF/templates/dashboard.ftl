<!DOCTYPE html>
<html>
<head>
    <title>Organization Rally Page</title>
    <meta charset="utf-8" />

    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
    <link rel="stylesheet" type="text/css" href="css/pure-grids-responsive-min.css" />
    <link rel="stylesheet" type="text/css" href="css/dashboard.css" />
    <link rel="stylesheet" href="https://i.icomoon.io/public/temp/da68616b0d/UntitledProject/style-svg.css">
    <script defer src="https://i.icomoon.io/public/temp/da68616b0d/UntitledProject/svgxuse.js"></script>

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
        <h2>Your rallies</h2>
        <form action="EditRally" method="post">
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
                            <td>
                                <span><a href="Rally/${rally.getId()}">${rally.getName()}</a></span><br />
                                <#if rally.getParent()??>
                                    <span class="parent-name">&#8627; Parent: <a href="Rally/${rally.getParent().getId()}">${rally.getParent().getName()}</a></span>
                                </#if>
                            </td>
                            <td>${rally.getStartTime()}</td>
                            <td>${rally.getLocation()}</td>
                            <td>
                                <button id="organizer-button" name="rally_id" class="pure-button" type="submit" value="${rally.getId()}">
                                    <svg class="icon"><use xlink:href="images/symbol-defs.svg#icon-pencil2"></use></svg> 
                                    Edit
                                </button>
                                <button id="organizer-button" onclick="return confirm('Are you sure you want to delete this item?');" name="rally_id_delete" class="pure-button" type="submit" value="${rally.getId()}">
                                    <svg class="icon"><use xlink:href="images/symbol-defs.svg#icon-bin"></use></svg> 
                                    Delete
                                </button>
                            </td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </form>
        
        <p>
            <a id="organizer-button" class="pure-button pure-button-primary" href="AddRally">Add Rally</a>
        </p>
    </main>

    <script type="text/javascript" async src="js/lib/svgxuse.min.js"></script>
</body>
</html>
