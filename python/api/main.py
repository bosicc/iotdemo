# Copyright 2015 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# [START app]
import logging
import datetime


from flask import Flask, request
# # from google.cloud import datastore

# http://stackoverflow.com/questions/20878885/google-appengine-ext-python-module-importerror-no-module-named-google-appengine


# import sys
# sys.path.insert(1, '/home/bopr/google-cloud-sdk/platform/google_appengine')
# sys.path.insert(1, '/home/bopr/google-cloud-sdk/platform/google_appengine/lib/yaml/lib')
# sys.path.insert(1, '/home/bopr/myapp/lib')
#
# if 'google' in sys.modules:
#      del sys.modules['google']
#      print("Deleted google from sys ")
#
# import google
# print(google.__path__)
from google.appengine.ext import ndb



app = Flask(__name__)


class Data(ndb.Model):
    data = ndb.StringProperty()
    time = ndb.DateProperty()


def add_entity_temperature(value):
    data = Data(
        data=value, time=datetime.datetime.now().date())
    return data.put()


@app.route('/')
def hello():
    """Return a friendly HTTP greeting."""
    return 'Our IoT demo REST API server is working!'


@app.route('/api/')
def api():
    """Return a friendly HTTP greeting."""
    return 'Just reply to simple GET request'


@app.route('/api/deviceinfo/<code>')
def get_device_info(code):
    s = '---------------------------------------------<br/>'
    s += '| Device number: ' + code + ' |<br/>'
    s += '---------------------------------------------<br/>'
    return s


@app.route('/api/temperature')
def get_temperature():
    val = 100
    return '{\"temperature\":' + str(val) + '}'


@app.route('/api/add/temperature', methods=['POST'])
def add_temperature():
    """request.data Contains the incoming request data as string in case it came with a mimetype Flask does not handle.
    request.args: the key/value pairs in the URL query string
    request.form: the key/value pairs in the body, as sent by a HTML POST form
    request.files: the files in the body, which Flask keeps separate from form.
    HTML forms must use enctype=multipart/form-data or files will not be uploaded.
    request.values: combined args and form, preferring args if keys overlap"""
    value = request.form.get('value')
    print("add_temperature() value=" + str(value))

    # ds = datastore.Client()
    #
    # entity = datastore.Entity(key=ds.key('test'))
    # entity.update({
    #     'temperature': value,
    #     'timestamp': datetime.datetime.utcnow()
    # })
    # ds.put(entity)

    res = add_entity_temperature(value)

    print("[Done], res=" + res)

    return '200 OK'


@app.errorhandler(500)
def server_error(e):
    logging.exception('An error occurred during a request.')
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500


if __name__ == '__main__':
    # This is used when running locally. Gunicorn is used to run the
    # application on Google App Engine. See entrypoint in app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)
# [END app]
