$LOAD_PATH.unshift File.expand_path('../../lib', __FILE__)
require 'jrgoogledata'
require 'json'

Creds = JSON.parse(File.read('./spec/account.json'), symbolize_names: true)
Creds[:application_name] = 'JrgooleData-Rspec'

Instances = Struct.new(:session, :workbook, :worksheet).new

# because the googlesheet can't rollback and their setup is expensive
#  we setup some global instances

def setup_session_instance
  Instances.session = JrGoogleData::Session.new(Creds)
end

def setup_workbook_instance
  setup_session_instance if Instances.session.nil?
  Instances.workbook = Instances.session.workbook_by_id('16QOG3WKn_WPUt1EYLE5_VkTPU82yDvpMqrc7jr40iis')
end

def setup_worksheet_instance
  setup_workbook_instance if Instances.workbook.nil?
  Instances.worksheet = Instances.workbook.worksheet_by_title('Main')
end
