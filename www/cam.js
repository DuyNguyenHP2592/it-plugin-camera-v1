    var argscheck = require('cordova/argscheck'),
        utils = require('cordova/utils'),
        exec = require('cordova/exec');

    var PLUGIN_NAME = "cam";

	module.exports = {
	
		setOnPictureTakenHandler : function (onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "setOnPictureTakenHandler", []);
		},

		setFlashMode: function (flashMode, onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "setFlashMode", [flashMode]);
		},

		setOnLogHandler : function (onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "wLog", []);
		},

		startCamera : function (options, onSuccess, onError) {
			options = options || {};
			if (typeof (options.x) === 'undefined') {
				options.x = 0;
			}
			if (typeof (options.y) === 'undefined') {
				options.y = 0;
			}
			if (typeof (options.width) === 'undefined') {
				options.width = window.screen.width;
			}
			if (typeof (options.height) === 'undefined') {
				options.height = window.screen.height;
			}
			if (typeof (options.camera) === 'undefined') {
				options.camera = 'back';
			}
			if (typeof (options.tapPhoto) === 'undefined') {
				options.tapPhoto = false;
			}
			if (typeof (options.previewDrag) === 'undefined') {
				options.previewDrag = false;
			}
			if (typeof (options.toBack) === 'undefined') {
				options.toBack = false;
			}
			if (typeof (options.alpha) === 'undefined') {
				options.alpha = 1;
			}

			exec(onSuccess, onError, PLUGIN_NAME, "startCamera", [options.x, options.y, options.width, options.height, options.camera, options.tapPhoto, options.previewDrag, options.toBack, options.alpha]);
		},

		stopCamera : function (onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "stopCamera", []);
		},

		takePhoto : function (dim, onSuccess, onError) {
			dim = dim || {};
			exec(onSuccess, onError, PLUGIN_NAME, "takePhoto", [dim.maxWidth || 0, dim.maxHeight || 0]);
		},

		setColorEffect : function (effect, onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "setColorEffect", [effect]);
		},

		switchCamera: function (onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "switchCamera", []);
		},

		hide: function (onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "hideCamera", []);
		},

		show: function (config, onSuccess, onError) {
			config = config || {};
			if (typeof (config.imageName) === 'undefined') {
				config.imageName = "IMG";
			}
			if (typeof (config.info) === 'undefined') {
				config.info = "";
			}
			exec(onSuccess, onError, PLUGIN_NAME, "showCamera", [config.imageName, config.info]);
		},
		//Hien thi theo Camera theo API 1
        show1: function (config, onSuccess, onError) {
			config = config || {};
			if (typeof (config.imageName) === 'undefined') {
				config.imageName = "IMG";
			}
			if (typeof (config.info) === 'undefined') {
				config.info = "";
			}
			exec(onSuccess, onError, PLUGIN_NAME, "showCamera1", [config.imageName, config.info]);
		},
		disable: function (disable, onSuccess, onError) {
			exec(onSuccess, onError, PLUGIN_NAME, "disable", [disable]);
		}

    };
