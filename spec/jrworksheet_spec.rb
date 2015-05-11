require 'spec_helper'

module JrGoogleData
  describe Worksheet do
    let(:session) { a_session() }
    let(:instance) { a_workbook(session).worksheet_by_title('Main') }

    context 'its public api' do
      let(:instance) { described_class.new }

      it 'has a constructor that takes no arguments' do
        expect{described_class.new}.not_to raise_error
      end

      it 'has an inspect method' do
        expect(instance).to respond_to(:inspect)
      end
    end

    context 'an instance' do
      it 'for a new instance, the inspect method shows bare contents' do
        expect(described_class.new.inspect).to match %r{#<JrGoogleData::Worksheet:0x(\p{XDigit}+)>}
      end

      it 'for a found instance, the inspect method shows url contents' do
        regex = %r{.+Worksheet:.+/feeds/cells.+/feeds/list.+>}
        expect(instance.inspect).to match regex
      end
    end
  end
end
