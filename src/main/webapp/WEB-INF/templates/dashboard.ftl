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
                    <th>EventName</th>
                    <th>EventTime</th>
                    <th>EventMaxPeople</th>
                    <th>EventCurrentPeople</th>
                    <th>&nbsp;</th>
                </tr>
            </thead>

            <tbody>
                <tr>
                    <td>APP presentation</td>
                    <td>APR/04</td>
                    <td>56</td>
                    <td>57</td>
                    <td><a id="organizer-button" class="pure-button" href="Add Rally">Edit Rally</a></td>
                </tr>
                <tr>
                    <td>MusicEvent</td>
                    <td>APR/01</td>
                    <td>100</td>
                    <td>78</td>
                    <td><a id="organizer-button" class="pure-button" href="Add Rally">Edit Rally</a></td>
                </tr>
            </tbody>
        </table>
        
        <p>
            <a id="organizer-button" class="pure-button pure-button-primary" href="Add Rally">Add Rally</a>
        </p>
    </main>
</body>
</html>
