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

        <nav class="pure-menu pure-menu-horizontal">
            <ul class="pure-menu-list">
                <li class="pure-menu-item"><a href="Dashboard" class="pure-menu-link">View your rallies</a></li>
                <li class="pure-menu-item pure-menu-selected"><a href="AddRally" class="pure-menu-link">Add new rally</a></li>
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
        <h2>Edit Rally</h2>
        <form class="pure-form pure-form-stacked pure-g" action="AddRally" method="post">
            <fieldset class="pure-u-1 pure-u-md-1-2">
            	<input 	type="hidden" name="id" value="${rally.getId()}">
                <div class="w-box-left">
                	
                    <label for="name">Event name</label>
                    <input class="pure-input-1" id="name" type="text" value="${rally.getName()}" name="name"required /> 
                    
                    <label for="name">Parent Rally</label>
               		
               		<select id="parentRally" name="parentRally" style="width:100%;">
               		 <option  value="0">No Parent</option>
               		 <#list rallylist as rallyfromlist>
               		 	<#if rally.getParent()?? && rallyfromlist.getId() == rally.getParent().getId()>
               		 		<option selected="selected" value="${rallyfromlist.getId()}">${rallyfromlist.getName()}</option>
              			<#else>
              			    <option value="${rallyfromlist.getId()}">${rallyfromlist.getName()}</option>
              			</#if>
              		  </#list>
					</select>
                    
                    <label for="description">Description</label>
                    <textarea class="pure-input-1" id="description" name="description" required>${rally.getDescription()}</textarea> 
                    
                    <label for="twitterHandle">Twitter username (optional)</label>
                    <input class="pure-input-1" id="twitterHandle" value="${rally.getTwitterHandle()}" type="text" name="twitterHandle" /> 
                    
                    <div class="pure-g">
                        <div class="pure-u-1-2">
                            <div class="w-box-left">
                                <label for="date">Event date</label>
                                <input class="pure-input-1" id="date" type="text" value="${rally.getDateTime()}" name="date" required /> 
                            </div>
                        </div>

                        <div class="pure-u-1-2">
                            <div class="w-box-right">
                                <label for="time">Event start time</label>
                                <input class="pure-input-1" id="time" type="text" name="time" required />
                            </div>
                        </div>
                    </div>

                    <br />
                    <input class="pure-button pure-button-primary" type="submit" value="Submit Event" />
                </div>
            </fieldset>

            <fieldset class="pure-u-1 pure-u-md-1-2">
                <div class="w-box-right">
                    <label for="us3-address">Event location</label>
                    <input class="pure-input-1" type="text" id="location" value="${rally.getLocation()}" name="location" required />

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
    <script type="text/javascript" async src="js/lib/svgxuse.min.js"></script>
    <script type="text/javascript">
        $('#date').datepicker();

        $('#time').timepicker({
            timeFormat: 'h:mm p',
            defaultTime: '${rally.getClockTime()}',
            dropdown: false
        });

        $('#us3').locationpicker({
            location: {
                latitude: ${rally.getLatitude()},
                longitude: ${rally.getLongitude()}
            },
            radius: 0,
            inputBinding: {
                latitudeInput: $('#latitude'),
                longitudeInput: $('#longitude'),
                locationNameInput: $('#location')
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
