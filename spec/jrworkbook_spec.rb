require 'spec_helper'

module JrGoogleData
  describe Workbook do
    let(:session) { a_session() }
    let(:instance) { a_workbook(session) }

    context 'its public api' do
      let(:instance) { described_class.new }

      it 'has a constructor that takes no arguments' do
        expect{described_class.new}.not_to raise_error
      end

      it 'has an inspect method' do
        expect(instance).to respond_to(:inspect)
      end
      
      it 'has a worksheet_by_title method' do
        expect(instance).to respond_to(:worksheet_by_title)
      end
    end

    context 'an instance' do
      it 'for a new instance, the inspect method shows bare contents' do
        expect(described_class.new.inspect).to match %r{#<JrGoogleData::Workbook:0x(\p{XDigit}+)>}
      end

      it 'for a found instance, the inspect method shows url contents' do
        regex = %r{#<JrGoogleData::Workbook:0x(\p{XDigit}+) id: https://spreadsheets.google.com/feeds/spreadsheets/16QOG3WKn_WPUt1EYLE5_VkTPU82yDvpMqrc7jr40iis, worksheetfeed_url: https://spreadsheets.google.com/feeds/worksheets/16QOG3WKn_WPUt1EYLE5_VkTPU82yDvpMqrc7jr40iis/private/full >}
        expect(instance.inspect).to match regex
      end

      it 'using a title, it raises an error when a worksheet is not found' do
        expect{instance.worksheet_by_title('Maain')}.to raise_error(
            JrGoogleData::ReadError, /Unable to find a worksheet with title: Maain/
          )
      end

      it 'using a title, it returns a found worksheet' do
        expect(instance.worksheet_by_title('Main')).not_to be nil
      end
    end
  end
end
