<!DOCTYPE html>
<html>
<head>
    <title>Add Rally Page</title>
    <meta charset="utf-8" />
 
    <link rel="stylesheet" type="text/css" href="css/pure-min.css" />
	<link rel="stylesheet" type="text/css" href="css/dashboard.css" />
	<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
	<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/timepicker/1.3.5/jquery.timepicker.min.css">
	<script type="text/javascript" src='https://maps.google.com/maps/api/js?key=AIzaSyBahO0DheLyHLVBMVrX7BQFZxxXevgGVxE&sensor=false&libraries=places'></script>
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="js/locationPicker/dist/locationpicker.jquery.min.js"></script>
 	 
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
                    
                    Date:<br>
                    <input class="pure-input-1" id="datepicker" type="text" name="datepicker" placeholder="datepicker" required /> 
                    
                    Event Start Time:<br>
                    <input class="pure-input-1" id="startTime" type="text" name="startTime" placeholder="startTime" required /> 
                   
                    <!--
                    Event area:<br>
                    <input class="pure-input-1" id="latitude" type="text" name="latitude" placeholder="latitude" required /> 
                    <input class="pure-input-1" id="longitude" type="text" name="longitude" placeholder="longitude" required /> 
					-->
					
					Event area:<br>
				    <div class="form-horizontal" style="width: 550px">
				        <div class="form-group">
				            <label class="col-sm-2 control-label"></label>
				
				            <div>
				                <input type="text" id="us3-address" />
				            </div>
				        </div>
				        <!--
				        <div class="form-group">
				        
				            <label class="col-sm-2 control-label">Radius:</label>
				
				            <div class="col-sm-5">
				                <input type="text" class="form-control" id="us3-radius" />
				            </div>
				        </div>
				        -->
				        <div id="us3" style="width: 550px; height: 300px;"></div>
				        <div class="clearfix">&nbsp;</div>
				        <div class="m-t-small" style="display: none;">
				            <label class="p-r-small col-sm-1 control-label">Lat.:</label>
				
				            <div class="col-sm-3">
				                <input type="text" class="form-control" style="width: 110px" id="latitude" name="latitude" />
				            </div>
				            <label class="p-r-small col-sm-2 control-label">Long.:</label>
				
				            <div class="col-sm-3">
				                <input type="text" class="form-control" style="width: 110px" id="longitude" name="longitude" />
				            </div>
				        </div>
				        <div class="clearfix"></div>
				        <script>
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
				                onchanged: function (currentLocation, radius, isMarkerDropped) {
				                    // Uncomment line below to show alert on each Location Changed event
				                    //alert("Location changed. New location (" + currentLocation.latitude + ", " + currentLocation.longitude + ")");
				                }
				            });
				        </script>
				    </div>

					<br>
                    <input class="pure-input-1 pure-button pure-button-primary" type="submit" value="Submit Event" />
                </fieldset>
            </form>                
        <p>
            <a id="organizer-button" class="pure-button pure-button-primary" href="submitRally">Submit</a>
        </p>
    </main>
	<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
	<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
	<script src="//cdnjs.cloudflare.com/ajax/libs/timepicker/1.3.5/jquery.timepicker.min.js"></script>
    <script>
	 $( "#datepicker" ).datepicker();
	 $('#startTime').timepicker({
    	timeFormat: 'h:mm p',
   		interval: 60,
    	minTime: '10',
    	maxTime: '6:00pm',
   		defaultTime: '11',
    	startTime: '10:00',
    	dynamic: false,
    	dropdown: true,
    	scrollbar: true
        });		
    </script>
</body>
</html>
