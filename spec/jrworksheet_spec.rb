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

      it 'has an new_list_query method' do
        expect(instance).to respond_to(:new_list_query)
      end

      it 'has an get_rows method' do
        expect(instance).to respond_to(:get_rows)
      end
    end

    context 'an instance' do
      it 'for a new instance, the inspect method shows bare contents' do
        expect(described_class.new.inspect).to match %r{#<JrGoogleData::Worksheet:0x(\p{XDigit}+)>}
      end

      it 'for a found instance, the inspect method shows url contents' do
        regex = %r{.+Worksheet:.+/feeds/cells.+/feeds/list.+>}
        actual = instance.inspect
        puts actual
        expect(actual).to match regex
      end

      it 'a found instance, returns a ListQuery object' do
        expect(instance.new_list_query).to be_a(ListQuery)
      end

      it 'given a loaded ListQuery object, it gets some rows' do
        qry = instance.new_list_query.add_start_index(1).add_max_results(2)
        results = instance.get_rows(qry)
        expect(results.size).to eq(2)
      end
    end
  end
end
