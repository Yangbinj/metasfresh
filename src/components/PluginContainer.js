import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { push } from 'react-router-redux';
import Container from '../components/Container';

const pluginWrapper = function pluginWrapper(WrappedComponent, ChildComponent) {
  return class WrappedPlugin extends Component {
    render() {
      return <WrappedComponent component={ChildComponent} {...this.props} />;
    }
  };
};

class PluginContainer extends Component {
  render() {
    const { rawModal, modal, component, dispatch, breadcrumb } = this.props;
    const TagName = component;
    const redirectPush = bindActionCreators(push, dispatch);

    return (
      <Container {...{ modal, rawModal }} breadcrumb={breadcrumb}>
        <div className="plugin-container" ref={c => (this.container = c)}>
          {TagName && (
            <TagName
              {...this.props}
              redirectPush={redirectPush}
              dispatch={dispatch}
            />
          )}
        </div>
      </Container>
    );
  }
}

function mapStateToProps(state) {
  const { windowHandler, menuHandler } = state;

  const { modal, rawModal } = windowHandler || {
    modal: {},
    rawModal: {},
  };

  return {
    modal,
    rawModal,
    breadcrumb: menuHandler.breadcrumb,
  };
}

export default connect(mapStateToProps)(PluginContainer);
export { pluginWrapper };
