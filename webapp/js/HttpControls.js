/**
 * @author mguimard
 *
 * Websocket remote controls
 *  - Install the app on your phone or tablet to use it
 */

THREE.HttpControls = function ( object ) {

	this.object = object;
	this.target = new THREE.Vector3( 0, 0, 0 );
	this.enabled = true;

	this.update = function( delta ) {
		if ( this.enabled === false ) return;
		
	};

};
