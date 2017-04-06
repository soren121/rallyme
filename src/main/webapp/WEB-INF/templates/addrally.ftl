<!DOCTYPE html>
<html>
<head>
    <title>Add Rally Page</title>
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
        <h1>Add Rally</h1>
        <a href="Login?logout=true" class="pure-button pure-button-primary">Logout</a>
	</header>

    <main>
        <form class="pure-form pure-form-stacked pure-g" action="AddRally" method="post">
            <fieldset class="pure-u-1 pure-u-md-1-2">
                <div class="l-box">
                    <label for="name">Event name</label>
                    <input class="pure-input-1" id="name" type="text" name="name" required /> 
                    
                    <label for="description">Description</label>
                    <textarea class="pure-input-1" id="description" name="description" required></textarea> 
                    
                    <label for="twitterHandle">Twitter username (optional)</label>
                    <input class="pure-input-1" id="twitterHandle" type="text" name="twitterHandle" /> 
                    
                    <div class="pure-g">
                        <div class="pure-u-1-2">
                            <label for="date">Event date</label>
                            <input class="pure-input-1" id="date" type="text" name="date" required /> 
                        </div>

                        <div class="pure-u-1-2">
                            <label for="time">Event start time</label>
                            <input class="pure-input-1" id="time" type="text" name="startTime" required />
                        </div>
                    </div>

                    <br />
                    <input class="pure-button pure-button-primary" type="submit" value="Submit Event" />
                </div>
            </fieldset>

            <fieldset class="pure-u-1 pure-u-md-1-2">
                <div class="l-box">
                    <label for="us3-address">Event location</label>
                    <input class="pure-input-1" type="text" id="us3-address" required />

                    <div id="us3" style="width: 100%; height: 350px;"></div>

                    <input type="hidden" id="latitude" name="latitude" required />
                    <input type="hidden" id="longitude" name="longitude" required />
                </div>
            </fieldset>
        </form>
    </main>

	<script type="text/javascript" src="js/lib/jquery-3.2.0.min.js"></script>
	<script type="text/javascript" src="js/lib/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/lib/jquery.timepicker.min.js"></script>
	<script type="text/javascript" src="https://maps.google.com/maps/api/js?key=AIzaSyBahO0DheLyHLVBMVrX7BQFZxxXevgGVxE&sensor=false&libraries=places"></script>
    <script type="text/javascript" src="js/lib/locationpicker.jquery.min.js"></script>
    <script type="text/javascript">
        $('#date').datepicker();

        $('#time').timepicker({
            timeFormat: 'h:mm p',
            defaultTime: '12:00 pm',
            dropdown: false
        });

        $('#us3').locationpicker({
            location: {
                latitude: 38.9071923,
                longitude: -77.03687070000001
            },
            radius: 0,
            inputBinding: {
                latitudeInput: $('#us3-lat'),
                longitudeInput: $('#us3-lon'),
                radiusInput: $('#us3-radius'),
                locationNameInput: $('#us3-address')
            },
            enableAutocomplete: true,
            onchanged: function(currentLocation, radius, isMarkerDropped) {
                // Uncomment line below to show alert on each Location Changed event
                //alert("Location changed. New location (" + currentLocation.latitude + ", " + currentLocation.longitude + ")");
            }
        });
    </script>
</body>
</html>
