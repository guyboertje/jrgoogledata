$LOAD_PATH.unshift File.expand_path('../../lib', __FILE__)
require 'jrgoogledata'
require 'json'

Creds = JSON.parse(File.read('./spec/account.json'), symbolize_names: true)
Creds[:application_name] = 'JrgooleData-Rspec'

def a_session
  JrGoogleData::Session.new(Creds)
end

def a_workbook(session = a_session())
  session.workbook_by_id('16QOG3WKn_WPUt1EYLE5_VkTPU82yDvpMqrc7jr40iis')
end
